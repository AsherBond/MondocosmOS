"""!
@package nviz_mapdisp.py

@brief wxGUI 3D view mode (map canvas)

This module implements 3D visualization mode for map display.

List of classes:
 - NvizThread
 - GLWindow
 - DragShape

(C) 2008-2011 by the GRASS Development Team

This program is free software under the GNU General Public
License (>=v2). Read the file COPYING that comes with GRASS
for details.

@author Martin Landa <landa.martin gmail.com> (Google SoC 2008/2010)
@author Anna Kratochvilova <kratochanna gmail.com> (Google SoC 2011)
"""

import os
import sys
import time
import copy
import math
import types

from threading import Thread

import wx
import wx.lib.scrolledpanel as scrolled
from wx.lib.newevent import NewEvent
from wx import glcanvas

import gcmd
import globalvar
from debug          import Debug
from mapdisp_window import MapWindow
from goutput        import wxCmdOutput
from preferences    import globalSettings as UserSettings
from workspace      import Nviz as NvizDefault

import wxnviz

wxUpdateProperties, EVT_UPDATE_PROP  = NewEvent()
wxUpdateView,       EVT_UPDATE_VIEW  = NewEvent()
wxUpdateLight,      EVT_UPDATE_LIGHT = NewEvent()
wxUpdateCPlane,     EVT_UPDATE_CPLANE = NewEvent()

class NvizThread(Thread):
    def __init__(self, log, progressbar, window):
        Thread.__init__(self)
        Debug.msg(5, "NvizThread.__init__():")
        self.log = log
        self.progressbar = progressbar
        self.window = window
        
        self._display = None
        
        self.setDaemon(True)

    def run(self):
        self._display = wxnviz.Nviz(self.log, self.progressbar)
        
    def GetDisplay(self):
        """!Get display instance"""
        return self._display

class GLWindow(MapWindow, glcanvas.GLCanvas):
    """!OpenGL canvas for Map Display Window"""
    def __init__(self, parent, id = wx.ID_ANY,
                 Map = None, tree = None, lmgr = None):
        self.parent = parent # MapFrame
        
        glcanvas.GLCanvas.__init__(self, parent, id)
        MapWindow.__init__(self, parent, id, 
                           Map, tree, lmgr)
        self.Hide()
        
        self.init = False
        self.initView = False
        
        # render mode 
        self.render = { 'quick' : False,
                        # do not render vector lines in quick mode
                        'vlines' : False,
                        'vpoints' : False,
                        'overlays': False }
        self.mouse = {
            'use': 'pointer'
            }
        self.cursors = {
            'default' : wx.StockCursor(wx.CURSOR_ARROW),
            'cross'   : wx.StockCursor(wx.CURSOR_CROSS),
            }
        # list of loaded map layers (layer tree items)
        self.layers  = list()
        # list of constant surfaces
        self.constants = list()
        # id of base surface (when vector is loaded and no surface exist)
        self.baseId = -1
        # list of cutting planes
        self.cplanes = list()
        # list of query points
        self.qpoints = list()
        # list of past views
        self.viewhistory  = []
        self.saveHistory = False
        # offset for dialog (e.g. DisplayAttributesDialog)
        self.dialogOffset = 5
        # overlays
        self.overlays = {}
        self.imagedict = {}
        self.overlay = wx.Overlay()
        self.pdc = wx.PseudoDC()
        self.textdict = {}
        self.dragid = None
        self.hitradius = 5
    
        if self.lmgr:
            self.log = self.lmgr.goutput
            logerr = self.lmgr.goutput.GetLog(err = True)
            logmsg = self.lmgr.goutput.GetLog()
        else:
            self.log = logmsg = sys.stdout
            logerr = sys.stderr
        
        # create nviz instance - use display region instead of computational
        os.environ['GRASS_REGION'] = self.Map.SetRegion(windres = True)
        
        self.nvizThread = NvizThread(logerr,
                                     self.parent.statusbarWin['progress'],
                                     logmsg)
        self.nvizThread.start()
        time.sleep(.1)
        self._display = self.nvizThread.GetDisplay()
        
        # GRASS_REGION needed only for initialization
        del os.environ['GRASS_REGION']
        
        self.img = wx.Image(self.Map.mapfile, wx.BITMAP_TYPE_ANY)
        
        # size of MapWindow, to avoid resizing if size is the same
        self.size = (0, 0)
        
        # default values
        self.nvizDefault = NvizDefault()
        self.view = copy.deepcopy(UserSettings.Get(group = 'nviz', key = 'view')) # copy
        self.iview = UserSettings.Get(group = 'nviz', key = 'view', internal = True)
        self.light = copy.deepcopy(UserSettings.Get(group = 'nviz', key = 'light')) # copy
        self.decoration = self.nvizDefault.SetDecorDefaultProp(type = 'arrow')
        self.decoration['scalebar'] = []
        self.decoration['arrow']['size'] = self._getDecorationSize()
        
        self.Bind(wx.EVT_ERASE_BACKGROUND, self.OnEraseBackground)
        self.Bind(wx.EVT_SIZE,             self.OnSize)
        self.Bind(wx.EVT_PAINT,            self.OnPaint)
        self.Bind(wx.EVT_IDLE,             self.OnIdle)
        self._bindMouseEvents()
        
        self.Bind(EVT_UPDATE_PROP,   self.UpdateMapObjProperties)
        self.Bind(EVT_UPDATE_VIEW,   self.UpdateView)
        self.Bind(EVT_UPDATE_LIGHT,  self.UpdateLight)
        self.Bind(EVT_UPDATE_CPLANE, self.UpdateCPlane)
        
        self.Bind(wx.EVT_CLOSE, self.OnClose)
        
        # cplanes cannot be initialized now
        wx.CallAfter(self.InitCPlanes)

    def __del__(self):
        self.UnloadDataLayers(force = True)
        
    def _bindMouseEvents(self):
        self.Bind(wx.EVT_MOUSE_EVENTS,     self.OnMouseAction)
        self.Bind(wx.EVT_MOTION,           self.OnMotion)
        
    def InitCPlanes(self):
        """!Initialize cutting planes list"""
        for i in range(self._display.GetCPlanesCount()):
            cplane = copy.deepcopy(UserSettings.Get(group = 'nviz', key = 'cplane'))
            cplane['on'] = False
            self.cplanes.append(cplane)
            
    def GetOverlay(self):
        """!Converts rendered overlay files to wx.Image
        
        Updates self.imagedict
        
        @return list of images
        """
        imgs = []
        for overlay in self.Map.GetListOfLayers(l_type = "overlay", l_active = True):
            if os.path.isfile(overlay.mapfile) and os.path.getsize(overlay.mapfile):
                img = wx.Image(overlay.mapfile, wx.BITMAP_TYPE_ANY)
                self.imagedict[img] = { 'id' : overlay.id,
                                        'layer' : overlay }
                imgs.append(img)

        return imgs        
    
    def OnClose(self, event):
        # cleanup when window actually closes (on quit) and not just is hidden
        self.UnloadDataLayers(force = True)
        
    def OnEraseBackground(self, event):
        pass # do nothing, to avoid flashing on MSW
    
    def OnSize(self, event):
        size = self.GetClientSize()
        if self.size != size \
            and self.GetContext():
            Debug.msg(3, "GLCanvas.OnSize(): w = %d, h = %d" % \
                      (size.width, size.height))
            self.SetCurrent()
            self._display.ResizeWindow(size.width,
                                       size.height)
        self.size = size
        event.Skip()
    
    def OnIdle(self, event):
        if self.render['overlays']:
            self.DrawOverlays()
            self.render['overlays'] = False
            
    def OnPaint(self, event):
        Debug.msg(1, "GLCanvas.OnPaint()")
        
        self.render['overlays'] = True
        dc = wx.PaintDC(self)
        self.DoPaint()
        

    def DoPaint(self):
        self.SetCurrent()
        
        if not self.initView:
            self._display.InitView()
            self.initView = True
        
        self.LoadDataLayers()
        self.UnloadDataLayers()
        
        if not self.init:
            self.ResetView()
            
            if hasattr(self.lmgr, "nviz"):
                self.lmgr.nviz.UpdatePage('view')
                self.lmgr.nviz.UpdatePage('light')
                self.lmgr.nviz.UpdatePage('cplane')
                self.lmgr.nviz.UpdatePage('decoration')
                layer = self.GetSelectedLayer()
                if layer:
                    if layer.type ==  'raster':
                        self.lmgr.nviz.UpdatePage('surface')
                        self.lmgr.nviz.UpdatePage('fringe')
                    elif layer.type ==  'vector':
                        self.lmgr.nviz.UpdatePage('vector')
                
                self.lmgr.nviz.UpdateSettings()
                
                # update widgets
                win = self.lmgr.nviz.FindWindowById( \
                    self.lmgr.nviz.win['vector']['lines']['surface'])
                win.SetItems(self.GetLayerNames('raster'))
            
            self.init = True
        
        self.UpdateMap()
    
    def DrawOverlays(self):
        """!Draw overlays with wx.Overlay"""
        dc = wx.ClientDC(self)
        odc = wx.DCOverlay(self.overlay, dc)
        self.pdc.Clear()
        self.pdc.RemoveAll()
        for img in self.GetOverlay():
            # draw any active and defined overlays
            if self.imagedict[img]['layer'].IsActive():
                id = self.imagedict[img]['id']
                self.DrawImage(img = img, drawid = id, coords = self.overlays[id]['coords'])
            
        for textId in self.textdict.keys():
            # create a bitmap the same size as our text
            self.DrawText(drawid = textId)
            
        self.pdc.DrawToDC(dc)
        del odc
        self.overlay.Reset()
        
    def DrawImage(self, img, drawid, coords):
        """!Draw overlay image"""
        bitmap = wx.BitmapFromImage(img)
        w,h = bitmap.GetSize()
        
        self.pdc.BeginDrawing()
        self.pdc.SetBackground(wx.TRANSPARENT_BRUSH)
        self.pdc.RemoveId(drawid)
        self.pdc.SetId(drawid)
        self.pdc.DrawBitmap(bitmap, coords[0], coords[1], True)
        self.pdc.SetIdBounds(drawid, wx.Rect(coords[0],coords[1], w, h))
        self.pdc.EndDrawing()
        
    def DrawText(self, drawid):
        """!Draw overlay text"""
        self.pdc.BeginDrawing()
        self.pdc.SetBackground(wx.TRANSPARENT_BRUSH)
        self.pdc.RemoveId(drawid)
        self.pdc.SetId(drawid)
        self.pdc.SetFont(self.textdict[drawid]['font'])
        self.pdc.SetTextForeground(self.textdict[drawid]['color'])
        if self.textdict[drawid]['rotation'] == 0:
            self.pdc.DrawText(self.textdict[drawid]['text'], self.textdict[drawid]['coords'][0],
                                                             self.textdict[drawid]['coords'][1])
        else:
            self.pdc.DrawRotatedText(self.textdict[drawid]['text'], self.textdict[drawid]['coords'][0],
                                                                    self.textdict[drawid]['coords'][1],
                                                                    self.textdict[drawid]['rotation'])
        self.pdc.SetIdBounds(drawid, self.textdict[drawid]['bbox'])
        self.pdc.EndDrawing()
        
    def UpdateOverlays(self):
        self.Map.ChangeMapSize(self.GetClientSize())
        self.Map.RenderOverlays(force = True)

        for textId in self.textdict.keys():
            coords, bbox, relCoords = self.TextBounds(self.textdict[textId])
            self.textdict[textId]['coords'] = coords
            self.textdict[textId]['bbox'] = bbox
        # rerender OnIdle
        self.render['overlays'] = True
                    
    def OnMouseAction(self, event):
        """!Handle mouse events"""
        # zoom with mouse wheel
        if event.GetWheelRotation() != 0:
            self.OnMouseWheel(event)
            
        # left mouse button pressed
        elif event.LeftDown():
            self.OnLeftDown(event)
        
        # left mouse button released
        elif event.LeftUp():
            self.OnLeftUp(event)
        
        # dragging
        elif event.Dragging():
            self.OnDragging(event)
            
        # double click    
        elif event.ButtonDClick():
            self.OnDClick(event)
        
        event.Skip()

    def OnMouseWheel(self, event):
        """!Change perspective"""
        wheel = event.GetWheelRotation()
        Debug.msg (5, "GLWindow.OnMouseMotion(): wheel = %d" % wheel)
        self.DoZoom(zoomtype = wheel, pos = event.GetPositionTuple())
            
        # update statusbar
        ### self.parent.StatusbarUpdate()
            
    def OnLeftDown(self, event):
        """!On left mouse down"""
        self.mouse['begin'] = event.GetPositionTuple()
        self.mouse['tmp'] = event.GetPositionTuple()
        if self.mouse['use'] == "lookHere":
            size = self.GetClientSize()
            self._display.LookHere(self.mouse['begin'][0], size[1] - self.mouse['begin'][1])
            focus = self._display.GetFocus()
            for i, coord in enumerate(('x', 'y', 'z')):
                self.iview['focus'][coord] = focus[i]
            self.saveHistory = True
            self.Refresh(False)
            toggle = self.lmgr.nviz.FindWindowByName('here')
            toggle.SetValue(False)
            self.mouse['use'] = 'pointer'
            self.SetCursor(self.cursors['default'])
                
        if self.mouse['use'] == 'arrow':
            pos = event.GetPosition()
            size = self.GetClientSize()
            self.SetDrawArrow((pos[0], size[1] - pos[1]))
                
        if self.mouse['use'] == 'scalebar':
            pos = event.GetPosition()
            size = self.GetClientSize()
            self.SetDrawScalebar((pos[0], size[1] - pos[1]))
        
        if self.mouse['use'] == 'pointer':
            # get decoration or text id
            self.dragid = None
            idlist = self.pdc.FindObjects(self.mouse['tmp'][0], self.mouse['tmp'][1],
                                          self.hitradius)                            
            if idlist != []:
                self.dragid = idlist[0] #drag whatever is on top
                
        event.Skip()    
        
    def OnDragging(self, event):
                
        if self.mouse['use'] == 'pointer':
            self.DragItem(self.dragid, event)
            
        if self.mouse['use'] == 'rotate':    
            dx, dy = event.GetX() - self.mouse['tmp'][0], event.GetY() - self.mouse['tmp'][1]
            self.mouse['tmp'] = event.GetPositionTuple()
            angle, x, y, z = self._display.GetRotationParameters(dx, dy)
            self._display.Rotate(angle, x, y, z)
            
            self.render['quick'] = True
            self.Refresh(False)
            
        if self.mouse['use'] == 'pan':
            self.FocusPanning(event)
                
        event.Skip()
            
    def Pixel2Cell(self, (x, y)):
        """!Convert image coordinates to real word coordinates

        @param x, y image coordinates
        
        @return easting, northing
        @return None on error
        """
        size = self.GetClientSize()
        # UL -> LL
        sid, x, y, z = self._display.GetPointOnSurface(x, size[1] - y)
        
        if not sid:
            return None
        
        return (x, y)
    
    def DoZoom(self, zoomtype, pos):
        """!Change perspective and focus"""
        
        prev_value = self.view['persp']['value']
        if zoomtype > 0:
            value = -1 * self.view['persp']['step']
        else:
            value = self.view['persp']['step']
        self.view['persp']['value'] +=  value
        if self.view['persp']['value'] < 1:
            self.view['persp']['value'] = 1
        elif self.view['persp']['value'] > 180:
            self.view['persp']['value'] = 180
        
        if prev_value !=  self.view['persp']['value']:
            if hasattr(self.lmgr, "nviz"):
                self.lmgr.nviz.UpdateSettings()
                x, y = pos[0], self.GetClientSize()[1] - pos[1]
                result = self._display.GetPointOnSurface(x, y)
                if result[0]:
                    self._display.LookHere(x, y)
                    focus = self._display.GetFocus()
                    for i, coord in enumerate(('x', 'y', 'z')):
                        self.iview['focus'][coord] = focus[i]
                self._display.SetView(self.view['position']['x'], self.view['position']['y'],
                                      self.iview['height']['value'],
                                      self.view['persp']['value'],
                                      self.view['twist']['value'])
                self.saveHistory = True
            # redraw map
            self.DoPaint()
            
    def OnLeftUp(self, event):
        self.mouse['end'] = event.GetPositionTuple()
        if self.mouse["use"] == "query":
            # querying
            layers = self.GetSelectedLayer(multi = True)
            isRaster = False
            nVectors = 0
            for l in layers:
                if l.GetType() == 'raster':
                    isRaster = True
                    break
                if l.GetType() == 'vector':
                    nVectors += 1
            
            if isRaster or nVectors > 1:
                self.OnQueryMap(event)
            else:
                self.OnQueryVector(event)
                    
        elif self.mouse["use"] in ('arrow', 'scalebar'):
            self.lmgr.nviz.FindWindowById(
                    self.lmgr.nviz.win['decoration'][self.mouse["use"]]['place']).SetValue(False)
            self.mouse['use'] = 'pointer'
            self.SetCursor(self.cursors['default'])
        elif self.mouse['use'] == 'pointer':
            if self.dragid < 99 and self.dragid in self.overlays:
                self.overlays[self.dragid]['coords'] = self.pdc.GetIdBounds(self.dragid)
            elif self.dragid > 100 and self.dragid in self.textdict:
                self.textdict[self.dragid]['bbox'] = self.pdc.GetIdBounds(self.dragid)
            if self.dragid:    
                self.dragid = None
                self.Refresh(False)
            
        elif self.mouse['use'] == 'rotate':
            self._display.UnsetRotation()
            self.iview['rotation'] = self._display.GetRotationMatrix()
            self.saveHistory = True
            self.render['quick'] = False
            self.Refresh(False)
        elif self.mouse['use'] == 'pan':
            self.saveHistory = True
            self.render['quick'] = False
            self.Refresh(False)
            
        elif self.mouse['use'] == 'zoom':
            self.DoZoom(zoomtype = self.zoomtype, pos = self.mouse['end'])
        event.Skip()
            
    def OnDClick(self, event):
        """!On mouse double click"""
        if self.mouse['use'] != 'pointer': return
        pos = event.GetPositionTuple()
        idlist  = self.pdc.FindObjects(pos[0], pos[1], self.hitradius)
        if idlist == []:
            return
        self.dragid = idlist[0]
        
        if self.dragid == 1:
            self.parent.OnAddLegend(None)
        elif self.dragid > 100:
            self.parent.OnAddText(None)
    
    def FocusPanning(self, event):
        """!Simulation of panning using focus"""
        size = self.GetClientSizeTuple()
        id1, x1, y1, z1 = self._display.GetPointOnSurface(
                      self.mouse['tmp'][0], size[1] - self.mouse['tmp'][1])
        id2, x2, y2, z2 = self._display.GetPointOnSurface(
                      event.GetX(), size[1] - event.GetY())
        if id1 and id1 == id2:
            dx, dy, dz = x2 - x1, y2 - y1, z2 - z1
            focus = self.iview['focus']
            focus['x'], focus['y'], focus['z'] = self._display.GetFocus()
            focus['x'] -= dx
            focus['y'] -= dy
            focus['z'] -= dz
            
            #update properties
            evt = wxUpdateView(zExag = False)
            wx.PostEvent(self, evt)
            
            self.mouse['tmp'] = event.GetPositionTuple()
            self.render['quick'] = True
            self.Refresh(False)
            
    def HorizontalPanning(self, event):
        """!Move all layers in horizontal (x, y) direction.
        Currently not used.
        """
        size = self.GetClientSizeTuple()
        id1, x1, y1, z1 = self._display.GetPointOnSurface(
                      self.mouse['tmp'][0], size[1] - self.mouse['tmp'][1])
        id2, x2, y2, z2 = self._display.GetPointOnSurface(
                      event.GetX(), size[1] - event.GetY())
        
        if id1 and id1 == id2:
            dx, dy = x2 - x1, y2 - y1
            # find raster and volume
            for item in self.layers:
                mapLayer = self.tree.GetPyData(item)[0]['maplayer']
                  
                data = self.tree.GetPyData(item)[0]['nviz']
                if mapLayer.GetType() == 'raster':
                    data['surface']['position']['x'] += dx
                    data['surface']['position']['y'] += dy
                    data['surface']['position']['update'] = None
                    
                    #update properties
                    evt = wxUpdateProperties(data = data)
                    wx.PostEvent(self, evt)
                    
                    if event.CmdDown() and id1 == data['surface']['object']['id']:
                        break
                    
                elif mapLayer.GetType() == '3d-raster':
                    if 'x' not in data['volume']['position']:
                        data['volume']['position']['x'] = 0
                        data['volume']['position']['y'] = 0
                        data['volume']['position']['z'] = 0
                    data['volume']['position']['x'] += dx
                    data['volume']['position']['y'] += dy
                    data['volume']['position']['update'] = None
                    
                    #update properties
                    evt = wxUpdateProperties(data = data)
                    wx.PostEvent(self, evt)
                
            self.mouse['tmp'] = event.GetPositionTuple()
            self.render['quick'] = True
            self.Refresh(False)
            
    def DragItem(self, id, event):
        """!Drag an overlay decoration item
        """
        if not id: return
        Debug.msg (5, "GLWindow.DragItem(): id=%d" % id)
    
        x, y = self.mouse['tmp']
        dx = event.GetX() - x
        dy = event.GetY() - y
        self.pdc.SetBackground(wx.TRANSPARENT_BRUSH)
        self.pdc.TranslateId(id, dx, dy)
        
        r2 = self.pdc.GetIdBounds(id)
        if type(r2) is list:
            r2 = wx.Rect(r[0], r[1], r[2], r[3])
        if id > 100: # text
            self.textdict[id]['bbox'] = r2
            self.textdict[id]['coords'][0] += dx
            self.textdict[id]['coords'][1] += dy
        else:
            self.overlays[id]['coords'] = r2
        
        dc = wx.ClientDC(self)
        odc = wx.DCOverlay(self.overlay, dc)
        odc.Clear()
        self.pdc.DrawToDC(dc)
        del odc
        self.mouse['tmp'] = (event.GetX(), event.GetY()) 
        
    def ZoomBack(self):
        """!Set previous view in history list
        """
        view = {}
        if len(self.viewhistory) > 1:
            self.viewhistory.pop()
            view = copy.deepcopy(self.viewhistory[-1])
        
        # disable tool if stack is empty
        if len(self.viewhistory) < 2: # disable tool
            toolbar = self.parent.toolbars['map']
            toolbar.Enable('zoomback', enable = False)
            
        # set view and update nviz view page
        self.lmgr.nviz.UpdateState(view = view[0], iview = view[1])
        self.lmgr.nviz.UpdatePage('view')
        # update map
        self.Refresh(False)

    def ViewHistory(self, view, iview):
        """!Manages a list of last 10 views
        
        @param view view dictionary
        @param iview view dictionary (internal)
        
        @return removed history item if exists (or None)
        """
        removed = None
        hview = copy.deepcopy(view)
        hiview = copy.deepcopy(iview)
        
        if not (self.viewhistory and self.viewhistory[-1] == (hview, hiview)):  
            self.viewhistory.append((hview, hiview))
            
        if len(self.viewhistory) > 10:
            removed = self.viewhistory.pop(0)
        
        if removed:
            Debug.msg(4, "GLWindow.ViewHistory(): hist=%s, removed=%s" %
                      (self.viewhistory, removed))
        else:
            Debug.msg(4, "GLWindow.ViewHistory(): hist=%s" %
                      (self.viewhistory))
        
        # update toolbar
        if len(self.viewhistory) > 1:
            enable = True
        else:
            enable = False
        
        toolbar = self.parent.toolbars['map']
        toolbar.Enable('zoomback', enable)
        
        return removed     
    
    def ResetViewHistory(self):
        """!Reset view history"""
        self.viewhistory = list()
    
    def GoTo(self, e, n):
        """!Focus on given point"""
        w = self.Map.region['w']
        s = self.Map.region['s']
        e -= w
        n -= s
        focus = self.iview['focus']
        focus['x'], focus['y'] = e, n
        self.saveHistory = True
        #update properties
        evt = wxUpdateView(zExag = False)
        wx.PostEvent(self, evt)
        
        self.render['quick'] = False
        self.Refresh(False)
    
    def OnQueryMap(self, event):
        """!Query raster and vector maps"""
        self.OnQuerySurface(event)
        self.parent.QueryMap(event.GetX(), event.GetY())
        
    def OnQuerySurface(self, event):
        """!Query surface on given position"""
        size = self.GetClientSizeTuple()
        result = self._display.QueryMap(event.GetX(), size[1] - event.GetY())
        if result:
            self.qpoints.append((result['x'], result['y'], result['z']))
            self.log.WriteLog("%-30s: %.3f" % (_("Easting"),   result['x']))
            self.log.WriteLog("%-30s: %.3f" % (_("Northing"),  result['y']))
            self.log.WriteLog("%-30s: %.3f" % (_("Elevation"), result['z']))
            name = ''
            for item in self.layers:
                self.tree.GetPyData(item)[0]['nviz']
                if self.tree.GetPyData(item)[0]['maplayer'].type == 'raster' and\
                    self.tree.GetPyData(item)[0]['nviz']['surface']['object']['id'] == result['id']:
                    name = self.tree.GetPyData(item)[0]['maplayer'].name
            self.log.WriteLog("%-30s: %s" % (_("Surface map name"), name))
            self.log.WriteLog("%-30s: %s" % (_("Surface map elevation"), result['elevation']))
            self.log.WriteLog("%-30s: %s" % (_("Surface map color"), result['color']))
            if len(self.qpoints) > 1:
                prev = self.qpoints[-2]
                curr = self.qpoints[-1]
                dxy = math.sqrt(pow(prev[0]-curr[0], 2) +
                                pow(prev[1]-curr[1], 2))
                dxyz = math.sqrt(pow(prev[0]-curr[0], 2) +
                                 pow(prev[1]-curr[1], 2) +
                                 pow(prev[2]-curr[2], 2))
                self.log.WriteLog("%-30s: %.3f" % (_("XY distance from previous"), dxy))
                self.log.WriteLog("%-30s: %.3f" % (_("XYZ distance from previous"), dxyz))
                self.log.WriteLog("%-30s: %.3f" % (_("Distance along surface"),
                                              self._display.GetDistanceAlongSurface(result['id'],
                                                                                    (curr[0], curr[1]),
                                                                                    (prev[0], prev[1]),
                                                                                    useExag = False)))
                self.log.WriteLog("%-30s: %.3f" % (_("Distance along exag. surface"),
                                              self._display.GetDistanceAlongSurface(result['id'],
                                                                                    (curr[0], curr[1]),
                                                                                    (prev[0], prev[1]),
                                                                                      useExag = True)))
            self.log.WriteCmdLog('-' * 80)
        else:
            self.log.WriteLog(_("No point on surface"))
            self.log.WriteCmdLog('-' * 80)
    
    def OnQueryVector(self, event):
        """!Query vector on given position"""
        self.parent.QueryVector(*event.GetPosition())
        
    def UpdateView(self, event):
        """!Change view settings"""
        data = self.view
        
        ## multiple z-exag value from slider by original value computed in ogsf so it scales
        ## correctly with maps of different height ranges        
        if event and event.zExag and 'value' in data['z-exag']:
            self._display.SetZExag(self.iview['z-exag']['original'] * data['z-exag']['value'])
            
        self._display.SetView(data['position']['x'], data['position']['y'],
                              self.iview['height']['value'],
                              data['persp']['value'],
                              data['twist']['value'])
        
        if self.iview['focus']['x'] != -1:
            self._display.SetFocus(self.iview['focus']['x'], self.iview['focus']['y'],
                                   self.iview['focus']['z'])
        if 'rotation' in self.iview:
            if self.iview['rotation']:
                self._display.SetRotationMatrix(self.iview['rotation'])
            else:
                self._display.ResetRotation()
        
        self.saveHistory = True
        if event:
            event.Skip()

    def UpdateLight(self, event):
        """!Change light settings"""
        data = self.light
        self._display.SetLight(x = data['position']['x'], y = data['position']['y'],
                               z = data['position']['z'] / 100., color = data['color'],
                               bright = data['bright'] / 100.,
                               ambient = data['ambient'] / 100.)
        self._display.DrawLightingModel()
        if event.refresh:
            self.Refresh(False)
        
    def UpdateMap(self, render = True):
        """!Updates the canvas anytime there is a change to the
        underlaying images or to the geometry of the canvas.
        
        @param render re-render map composition
        """
        start = time.clock()
        
        self.resize = False
        
        if self.render['quick'] is False:
            self.parent.statusbarWin['progress'].Show()
            self.parent.statusbarWin['progress'].SetRange(2)
            self.parent.statusbarWin['progress'].SetValue(0)
        
        if self.render['quick'] is False:
            self.parent.statusbarWin['progress'].SetValue(1)
            self._display.Draw(False, -1)
            if self.saveHistory:
                self.ViewHistory(view = self.view, iview = self.iview)
                self.saveHistory = False
        elif self.render['quick'] is True:
            # quick
            mode = wxnviz.DRAW_QUICK_SURFACE | wxnviz.DRAW_QUICK_VOLUME
            if self.render['vlines']:
                mode |=  wxnviz.DRAW_QUICK_VLINES
            if self.render['vpoints']:
                mode |=  wxnviz.DRAW_QUICK_VPOINTS
            self._display.Draw(True, mode)
        else: # None -> reuse last rendered image
            pass # TODO
            
        self.SwapBuffers()
        # draw fringe after SwapBuffers, otherwise it don't have to be visible
        # on some computers
        if self.render['quick'] is False:
            self._display.DrawFringe()
            if self.decoration['arrow']['show']:
                self._display.DrawArrow()
            if self.decoration['scalebar']:
                self._display.DrawScalebar()
        
        stop = time.clock()
        
        if self.render['quick'] is False:
            self.parent.statusbarWin['progress'].SetValue(2)
            # hide process bar
            self.parent.statusbarWin['progress'].Hide()
        
        Debug.msg(3, "GLWindow.UpdateMap(): quick = %d, -> time = %g" % \
                      (self.render['quick'], (stop-start)))
        
    def EraseMap(self):
        """!Erase the canvas
        """
        self._display.EraseMap()
        self.SwapBuffers()
    
    def _getDecorationSize(self):
        """!Get initial size of north arrow/scalebar"""
        size = self._display.GetLongDim() / 8.
        coef = 0.01
        if size < 1:
            coef = 100.
        return int(size * coef)/coef
    
    def SetDrawArrow(self, pos):
        
        if self._display.SetArrow(pos[0], pos[1], 
                                 self.decoration['arrow']['size'],
                                 self.decoration['arrow']['color']):
            self._display.DrawArrow()
            # update
            self.decoration['arrow']['show'] = True
            self.decoration['arrow']['position']['x'] = pos[0]
            self.decoration['arrow']['position']['y'] = pos[1]
            self.Refresh(False)
    
    def SetDrawScalebar(self, pos):
        """!Add scale bar, sets properties and draw"""
        if len(self.decoration['scalebar']) == 0:
            self.decoration['scalebar'].append(
                    self.nvizDefault.SetDecorDefaultProp(type = 'scalebar')['scalebar'])
            self.decoration['scalebar'][0]['size'] = self._getDecorationSize()
        else:
            self.decoration['scalebar'].append(copy.deepcopy(self.decoration['scalebar'][-1]))
            self.decoration['scalebar'][-1]['id'] += 1
        
        ret = self._display.SetScalebar(self.decoration['scalebar'][-1]['id'], pos[0], pos[1], 
                                 self.decoration['scalebar'][-1]['size'],
                                 self.decoration['scalebar'][-1]['color'])
        if ret:
            self._display.DrawScalebar()
            # update
            self.decoration['scalebar'][-1]['position']['x'] = pos[0]
            self.decoration['scalebar'][-1]['position']['y'] = pos[1]
            self.Refresh(False)
        
    def IsLoaded(self, item):
        """!Check if layer (item) is already loaded
        
        @param item layer item
        """
        layer = self.tree.GetPyData(item)[0]['maplayer']
        data = self.tree.GetPyData(item)[0]['nviz']
        
        if not data:
            return 0
        
        if layer.type ==  'raster':
            if 'object' not in data['surface']:
                return 0
        elif layer.type ==  'vector':
            if 'object' not in data['vlines'] and \
                    'object' not in data['points']:
                return 0
        
        return 1

    def _GetDataLayers(self, item, litems):
        """!Return get list of enabled map layers"""
        # load raster & vector maps
        while item and item.IsOk():
            type = self.tree.GetPyData(item)[0]['type']
            if type ==  'group':
                subItem = self.tree.GetFirstChild(item)[0]
                self._GetDataLayers(subItem, litems)
                item = self.tree.GetNextSibling(item)
                
            if not item.IsChecked() or \
                    type not in ('raster', 'vector', '3d-raster'):
                item = self.tree.GetNextSibling(item)
                continue
            
            litems.append(item)
            
            item = self.tree.GetNextSibling(item)
        
    def LoadDataLayers(self):
        """!Load raster/vector from current layer tree
        
        @todo volumes
        """
        if not self.tree:
            return
        
        listOfItems = []
        item = self.tree.GetFirstChild(self.tree.root)[0]
        self._GetDataLayers(item, listOfItems)
        
        start = time.time()
        
        while(len(listOfItems) > 0):
            item = listOfItems.pop()
            type = self.tree.GetPyData(item)[0]['type']
            if item in self.layers:
                continue
            # "raster (double click to set properties)" - tries to load this 
            # layer - no idea how to fix it
            if ' ' in self.tree.GetPyData(item)[0]['maplayer'].name:
                return
            try:
                if type ==  'raster':
                    self.LoadRaster(item)
                elif type ==  '3d-raster':
                    self.LoadRaster3d(item)
                elif type ==  'vector':
                    # data = self.tree.GetPyData(item)[0]['nviz']
                    # vecType = []
                    # if data and 'vector' in data:
                    #     for v in ('lines', 'points'):
                    #         if data['vector'][v]:
                    #             vecType.append(v)
                    layer = self.tree.GetPyData(item)[0]['maplayer']
                    npoints, nlines, nfeatures, mapIs3D = self.lmgr.nviz.VectorInfo(layer)
                    if npoints > 0:
                        self.LoadVector(item, points = True)
                    if nlines > 0:
                        self.LoadVector(item, points = False)
            except gcmd.GException, e:
                GError(parent = self,
                       message = e.value)
            self.init = False
        
        stop = time.time()
        
        Debug.msg(1, "GLWindow.LoadDataLayers(): time = %f" % (stop-start))
                
    def UnloadDataLayers(self, force = False):
        """!Unload any layers that have been deleted from layer tree

        @param force True to unload all data layers
        """
        if not self.tree:
            return
        
        listOfItems = []
        if not force:
            item = self.tree.GetFirstChild(self.tree.root)[0]
            self._GetDataLayers(item, listOfItems)
        
        start = time.time()
        
        update = False
        layersTmp = self.layers[:]
        for layer in layersTmp:
            if layer in listOfItems:
                continue
            ltype = self.tree.GetPyData(layer)[0]['type']
            try:
                if ltype ==  'raster':
                    self.UnloadRaster(layer)
                elif ltype ==  '3d-raster':
                    self.UnloadRaster3d(layer) 
                elif ltype ==  'vector':
                    maplayer = self.tree.GetPyData(layer)[0]['maplayer']
                    npoints, nlines, nfeatures, mapIs3D = self.lmgr.nviz.VectorInfo(maplayer)
                    if npoints > 0:
                        self.UnloadVector(layer, points = True)
                    if nlines > 0:
                        self.UnloadVector(layer, points = False)
                        
            except gcmd.GException, e:
                gcmd.GError(parent = self,
                            message = e.value)
        
        if force and self.baseId > 0: # unload base surface when quitting
            ret = self._display.UnloadSurface(self.baseId)
            self.baseId = -1
        if update:
            self.lmgr.nviz.UpdateSettings()        
            self.UpdateView(None)
        
        stop = time.time()
        
        Debug.msg(1, "GLWindow.UnloadDataLayers(): time = %f" % (stop-start))        
        
    def SetVectorSurface(self, data):
        """!Set reference surfaces of vector"""
        data['mode']['surface'] = {}
        data['mode']['surface']['value'] = list()
        data['mode']['surface']['show'] = list()
        for name in self.GetLayerNames('raster'):
            data['mode']['surface']['value'].append(name)
            data['mode']['surface']['show'].append(True)
        
    def SetVectorFromCmd(self, item, data):
        """!Set 3D view properties from cmd (d.vect)

        @param item Layer Tree item
        @param nviz data
        """
        cmd = self.tree.GetPyData(item)[0]['cmd']
        if cmd[0] != 'd.vect':
            return
        for opt in cmd[1:]:
            try:
                key, value = opt.split('=')
            except ValueError:
                continue
            if key == 'color':
                data['lines']['color']['value'] = value
                data['points']['color']['value'] = value

    def SetMapObjProperties(self, item, id, nvizType):
        """!Set map object properties
        
        Properties must be afterwards updated by
        UpdateMapObjProperties().
        
        @param item layer item
        @param id nviz layer id (or -1)
        @param nvizType nviz data type (surface, points, vector)
        """
        if nvizType != 'constant':
            mapType = self.tree.GetPyData(item)[0]['maplayer'].type
            # reference to original layer properties (can be None)
            data = self.tree.GetPyData(item)[0]['nviz']
        else:
            mapType = nvizType
            data = self.constants[item]
        
        if not data:
            # init data structure
            if nvizType != 'constant':
                self.tree.GetPyData(item)[0]['nviz'] = {}
                data = self.tree.GetPyData(item)[0]['nviz']
            
            if mapType ==  'raster':
                # reset to default properties
                data[nvizType] = self.nvizDefault.SetSurfaceDefaultProp()
                        
            elif mapType ==  'vector':
                # reset to default properties (lines/points)
                data['vector'] = self.nvizDefault.SetVectorDefaultProp()
                self.SetVectorFromCmd(item, data['vector'])
                self.SetVectorSurface(data['vector']['points'])
                self.SetVectorSurface(data['vector']['lines'])
                
            elif mapType ==  '3d-raster':
                # reset to default properties 
                data[nvizType] = self.nvizDefault.SetVolumeDefaultProp()
                
            elif mapType == 'constant':
                data['constant'] = self.nvizDefault.SetConstantDefaultProp()
        
        else:
            # complete data (use default values), not sure if this is necessary
            if mapType ==  'raster':
                if not data['surface']:
                    data['surface'] = self.nvizDefault.SetSurfaceDefaultProp()
            if mapType ==  'vector':
                if not data['vector']['lines']:
                    self.nvizDefault.SetVectorLinesDefaultProp(data['vector']['lines'])
                if not data['vector']['points']:
                    self.nvizDefault.SetVectorPointsDefaultProp(data['vector']['points'])     
            # set updates
            for sec in data.keys():
                for sec1 in data[sec].keys():
                    if sec1 == 'position':
                        data[sec][sec1]['update'] = None
                        continue
                    if type(data[sec][sec1]) == types.DictType:
                        for sec2 in data[sec][sec1].keys():
                            if sec2 not in ('all', 'init', 'id'):
                                data[sec][sec1][sec2]['update'] = None
                    elif type(data[sec][sec1]) == types.ListType:
                        for i in range(len(data[sec][sec1])):
                            for sec2 in data[sec][sec1][i].keys():
                                data[sec][sec1][i][sec2]['update'] = None
            event = wxUpdateProperties(data = data)
            wx.PostEvent(self, event)
        
        # set id
        if id > 0:
            if mapType in ('raster', '3d-raster'):
                data[nvizType]['object'] = { 'id' : id,
                                            'init' : False }
            elif mapType ==  'vector':
                data['vector'][nvizType]['object'] = { 'id' : id,
                                                       'init' : False }
            elif mapType ==  'constant':
                data[nvizType]['object'] = { 'id' : id,
                                             'init' : False }
        
        return data

    def LoadRaster(self, item):
        """!Load 2d raster map and set surface attributes
        
        @param layer item
        """
        return self._loadRaster(item)
    
    def LoadRaster3d(self, item):
        """!Load 3d raster map and set surface attributes
        
        @param layer item
        """
        return self._loadRaster(item)
    
    def _loadRaster(self, item):
        """!Load 2d/3d raster map and set its attributes
        
        @param layer item
        """
        layer = self.tree.GetPyData(item)[0]['maplayer']
        
        if layer.type not in ('raster', '3d-raster'):
            return
        
        if layer.type ==  'raster':
            id = self._display.LoadSurface(str(layer.name), None, None)
            nvizType = 'surface'
            errorMsg = _("Loading raster map")
        elif layer.type ==  '3d-raster':
            id = self._display.LoadVolume(str(layer.name), None, None)
            nvizType = 'volume'
            errorMsg = _("Loading 3d raster map")
        else:
            id = -1
        
        if id < 0:
            if layer.type in ('raster', '3d-raster'):
                self.log.WriteError("%s <%s> %s" % (errorMsg, layer.name, _("failed")))
            else:
                self.log.WriteError(_("Unsupported layer type '%s'") % layer.type)
        
        self.layers.append(item)
        
        # set default/workspace layer properties
        data = self.SetMapObjProperties(item, id, nvizType)
        
        # update properties
        event = wxUpdateProperties(data = data)
        wx.PostEvent(self, event)
        
        # update tools window
        if hasattr(self.lmgr, "nviz") and \
                item ==  self.GetSelectedLayer(type = 'item'):
            toolWin = self.lmgr.nviz
            if layer.type ==  'raster':
                win = toolWin.FindWindowById( \
                    toolWin.win['vector']['lines']['surface'])
                win.SetItems(self.GetLayerNames(layer.type))
            
            #toolWin.UpdatePage(nvizType)
            #toolWin.SetPage(nvizType)
        
        return id
    
    def NewConstant(self):
        """!Create new constant"""
        index = len(self.constants)
        try:
            name = self.constants[-1]['constant']['object']['name'] + 1
        except IndexError:
            name = 1
        data = dict()
        self.constants.append(data)
        data = self.SetMapObjProperties(item = index, id = -1, nvizType = 'constant')
        self.AddConstant(data, name)
        return name
        
    def AddConstant(self, data, name):
        """!Add new constant"""
        id = self._display.AddConstant(value = data['constant']['value'], color = data['constant']['color'])
        self._display.SetSurfaceRes(id, data['constant']['resolution'], data['constant']['resolution'])
        data['constant']['object'] = { 'id' : id,
                                       'name': name,
                                       'init' : False }
    
    def DeleteConstant(self, index):
        """!Delete constant layer"""
        id = self.constants[index]['constant']['object']['id']
        self._display.UnloadSurface(id)
        del self.constants[index]
    
    def SelectCPlane(self, index):
        """!Select cutting plane"""
        for plane in range (self._display.GetCPlanesCount()):
            if plane == index:
                self._display.SelectCPlane(plane)
                self.cplanes[plane]['on'] = True
                self._display.SetFenceColor(self.cplanes[plane]['shading'])
            else:
                self._display.UnselectCPlane(plane)
                try:
                    self.cplanes[plane]['on'] = False
                except IndexError:
                    pass
                    
    def UpdateCPlane(self, event):
        """!Change cutting plane settings"""
        current = event.current
        for each in event.update:
            if each == 'rotation':
                self._display.SetCPlaneRotation(0, self.cplanes[current]['rotation']['tilt'],
                                                   self.cplanes[current]['rotation']['rot'])
            if each == 'position':
                self._display.SetCPlaneTranslation(self.cplanes[current]['position']['x'],
                                                   self.cplanes[current]['position']['y'],
                                                   self.cplanes[current]['position']['z'])
            if each == 'shading':
                self._display.SetFenceColor(self.cplanes[current]['shading'])
            
    def UnloadRaster(self, item):
        """!Unload 2d raster map
        
        @param layer item
        """
        return self._unloadRaster(item)
    
    def UnloadRaster3d(self, item):
        """!Unload 3d raster map
        
        @param layer item
        """
        return self._unloadRaster(item)
    
    def _unloadRaster(self, item):
        """!Unload 2d/3d raster map
        
        @param item layer item
        """
        layer = self.tree.GetPyData(item)[0]['maplayer']
        
        if layer.type not in ('raster', '3d-raster'):
            return
        
        data = self.tree.GetPyData(item)[0]['nviz']
        
        if layer.type ==  'raster':
            nvizType = 'surface'
            unloadFn = self._display.UnloadSurface
            errorMsg = _("Unable to unload raster map")
            successMsg = _("Raster map")
        else:
            nvizType = 'volume'
            unloadFn = self._display.UnloadVolume
            errorMsg = _("Unable to unload 3d raster map")
            successMsg = _("3d raster map")
        
        try:
            id = data[nvizType]['object']['id']
        except KeyError:
            return
        
        if unloadFn(id) ==  0:
            self.log.WriteError("%s <%s>" % (errorMsg, layer.name))
        else:
            self.log.WriteLog("%s <%s> %s" % (successMsg, layer.name, _("unloaded successfully")))
        
        data[nvizType].pop('object')
        
        self.layers.remove(item)
        
        # update tools window
        if hasattr(self.lmgr, "nviz"):
            toolWin = self.lmgr.nviz
            if layer.type ==  'raster':
                win = toolWin.FindWindowById(toolWin.win['vector']['lines']['surface'])
                win.SetItems(self.GetLayerNames(layer.type))
                win = toolWin.FindWindowById(toolWin.win['surface']['map'])
                win.SetValue('')
            if layer.type ==  '3d-raster':
                win = toolWin.FindWindowById(toolWin.win['volume']['map'])
                win.SetValue('')
            if layer.type ==  'vector':
                win = toolWin.FindWindowById(toolWin.win['vector']['map'])
                win.SetValue('')
        
    def LoadVector(self, item, points = None, append = True):
        """!Load 2D or 3D vector map overlay
        
        @param item layer item
        @param points True to load points, False to load lines, None
        to load both
        @param append append vector to layer list
        """
        layer = self.tree.GetPyData(item)[0]['maplayer']
        if layer.type !=  'vector':
            return
        
        # set default properties
        if points is None:
            self.SetMapObjProperties(item, -1, 'lines')
            self.SetMapObjProperties(item, -1, 'points')
            vecTypes = ('points', 'lines')
        elif points:
            self.SetMapObjProperties(item, -1, 'points')
            vecTypes = ('points', )
        else:
            self.SetMapObjProperties(item, -1, 'lines')
            vecTypes = ('lines', )
        
        id = -1
        for vecType in vecTypes:
            if vecType == 'lines':
                id, baseId = self._display.LoadVector(str(layer.GetName()), False)
            else:
                id, baseId = self._display.LoadVector(str(layer.GetName()), True)
            if id < 0:
                self.log.WriteError(_("Loading vector map <%(name)s> (%(type)s) failed") % \
                    { 'name' : layer.name, 'type' : vecType })
            # update layer properties
            self.SetMapObjProperties(item, id, vecType)
        if baseId > 0:
            self.baseId = baseId # id of base surface (when no surface is loaded)
        if append:
            self.layers.append(item)
        
        # update properties
        data = self.tree.GetPyData(item)[0]['nviz']
        event = wxUpdateProperties(data = data)
        wx.PostEvent(self, event)
        
        # update tools window
        if hasattr(self.lmgr, "nviz") and \
                item ==  self.GetSelectedLayer(type = 'item'):
            toolWin = self.lmgr.nviz
            
            toolWin.UpdatePage('vector')
            ### toolWin.SetPage('vector')
        
        return id

    def UnloadVector(self, item, points = None, remove = True):
        """!Unload vector map overlay
        
        @param item layer item
        @param points,lines True to unload given feature type
        @param remove remove layer from list
        """
        layer = self.tree.GetPyData(item)[0]['maplayer']
        data = self.tree.GetPyData(item)[0]['nviz']['vector']
        
        # if vecType is None:
        #     vecType = []
        #     for v in ('lines', 'points'):
        #         if UserSettings.Get(group = 'nviz', key = 'vector',
        #                             subkey = [v, 'show']):
        #             vecType.append(v)
        
        if points is None:
            vecTypes = ('points', 'lines')
        elif points:
            vecTypes = ('points', )
        else:
            vecTypes = ('lines', )
        
        for vecType in vecTypes:
            if 'object' not in data[vecType]:
                continue

            id = data[vecType]['object']['id']
            
            if vecType ==  'lines':
                ret = self._display.UnloadVector(id, False)
            else:
                ret = self._display.UnloadVector(id, True)
            if ret ==  0:
                self.log.WriteError(_("Unable to unload vector map <%(name)s> (%(type)s)") % \
                    { 'name': layer.name, 'type' : vecType })
            else:
                self.log.WriteLog(_("Vector map <%(name)s> (%(type)s) unloaded successfully") % \
                    { 'name' : layer.name, 'type' : vecType })
            
            data[vecType].pop('object')
            
        if remove and item in self.layers:
            self.layers.remove(item)
        
    def OnZoomToMap(self, event):
        """!Set display extents to match selected raster or vector
        map or volume.
        
        @todo vector, volume
        """
        layer = self.GetSelectedLayer()
        
        if layer is None:
            return
        
        Debug.msg (3, "GLWindow.OnZoomToMap(): layer = %s, type = %s" % \
                       (layer.name, layer.type))
        
        self._display.SetViewportDefault()

    def ResetView(self):
        """!Reset to default view"""
        self.iview['z-exag']['original'], \
            self.iview['height']['value'], \
            self.iview['height']['min'], \
            self.iview['height']['max'] = self._display.SetViewDefault()
            
        ## set initial z-exag value at 1X    
        self.view['z-exag']['value'] = 1.0
        
        self.view['z-exag']['min'] = UserSettings.Get(group = 'nviz', key = 'view',
                                                      subkey = ('z-exag', 'min'))        
        zexagMax = UserSettings.Get(group = 'nviz', key = 'view',
                                    subkey = ('z-exag', 'max'))        
        
        self.view['position']['x'] = UserSettings.Get(group = 'nviz', key = 'view',
                                                 subkey = ('position', 'x'))
        self.view['position']['y'] = UserSettings.Get(group = 'nviz', key = 'view',
                                                 subkey = ('position', 'y'))
        self.view['persp']['value'] = UserSettings.Get(group = 'nviz', key = 'view',
                                                       subkey = ('persp', 'value'))
        
        self.view['twist']['value'] = UserSettings.Get(group = 'nviz', key = 'view',
                                                       subkey = ('twist', 'value'))
        self._display.ResetRotation()
        self.iview['rotation'] = None
        self._display.LookAtCenter()
        focus = self.iview['focus']
        focus['x'], focus['y'], focus['z'] = self._display.GetFocus()
        
        event = wxUpdateView(zExag = False)
        wx.PostEvent(self, event)
        
    def UpdateMapObjProperties(self, event):
        """!Generic method to update data layer properties"""
        data = event.data
        
        if 'surface' in data:
            try:
                id = data['surface']['object']['id']
            except KeyError:
                return
            self.UpdateSurfaceProperties(id, data['surface'])
            # -> initialized
            data['surface']['object']['init'] = True
            
        elif 'constant' in data:
            id = data['constant']['object']['id']
            self.UpdateConstantProperties(id, data['constant'])
            # -> initialized
            data['constant']['object']['init'] = True  
              
        elif 'volume' in data:
            id = data['volume']['object']['id']
            self.UpdateVolumeProperties(id, data['volume'])
            # -> initialized
            data['volume']['object']['init'] = True
            
        elif 'vector' in data:
            for type in ('lines', 'points'):
                if 'object' in data['vector'][type]:
                    id = data['vector'][type]['object']['id']
                    self.UpdateVectorProperties(id, data['vector'], type)
                    # -> initialized
                    data['vector'][type]['object']['init'] = True
    
    def UpdateConstantProperties(self, id, data):
        """!Update surface map object properties"""
        self._display.SetSurfaceColor(id = id, map = False, value = data['color'])
        self._display.SetSurfaceTopo(id = id, map = False, value = data['value'])
        self._display.SetSurfaceRes(id, data['resolution'], data['resolution'])
        if data['transp'] == 0:
            self._display.UnsetSurfaceTransp(id)
        else:
            self._display.SetSurfaceTransp(id, map = False, value = data['transp'])
            
    def UpdateSurfaceProperties(self, id, data):
        """!Update surface map object properties"""
        # surface attributes
        for attrb in ('color', 'mask',
                     'transp', 'shine'):
            if attrb not in data['attribute'] or \
                    'update' not in data['attribute'][attrb]:
                continue
            
            map = data['attribute'][attrb]['map']
            value = data['attribute'][attrb]['value']
            
            if map is None: # unset
                # only optional attributes
                if attrb ==  'mask':
                    # TODO: invert mask
                    # TODO: broken in NVIZ
                    self._display.UnsetSurfaceMask(id)
                elif attrb ==  'transp':
                    self._display.UnsetSurfaceTransp(id) 
            else:
                if type(value) == types.StringType and \
                        len(value) <=  0: # ignore empty values (TODO: warning)
                    continue
                if attrb ==  'color':
                    self._display.SetSurfaceColor(id, map, str(value))
                elif attrb ==  'mask':
                    # TODO: invert mask
                    # TODO: broken in NVIZ
                    self._display.SetSurfaceMask(id, False, str(value))
                elif attrb ==  'transp':
                    self._display.SetSurfaceTransp(id, map, str(value)) 
                elif attrb ==  'shine':
                    self._display.SetSurfaceShine(id, map, str(value)) 
            data['attribute'][attrb].pop('update')
        
        # draw res
        if 'update' in data['draw']['resolution']:
            coarse = data['draw']['resolution']['coarse']
            fine   = data['draw']['resolution']['fine']
            
            if data['draw']['all']:
                self._display.SetSurfaceRes(-1, fine, coarse)
            else:
                self._display.SetSurfaceRes(id, fine, coarse)
            data['draw']['resolution'].pop('update')
        
        # draw style
        if 'update' in data['draw']['mode']:
            if data['draw']['mode']['value'] < 0: # need to calculate
                data['draw']['mode']['value'] = \
                    self.nvizDefault.GetDrawMode(mode = data['draw']['mode']['desc']['mode'],
                                                 style = data['draw']['mode']['desc']['style'],
                                                 shade = data['draw']['mode']['desc']['shading'],
                                                 string = True)
            style = data['draw']['mode']['value']
            if data['draw']['all']:
                self._display.SetSurfaceStyle(-1, style)
            else:
                self._display.SetSurfaceStyle(id, style)
            data['draw']['mode'].pop('update')
        
        # wire color
        if 'update' in data['draw']['wire-color']:
            color = data['draw']['wire-color']['value']
            if data['draw']['all']:
                self._display.SetWireColor(-1, str(color))
            else:
                self._display.SetWireColor(id, str(color))
            data['draw']['wire-color'].pop('update')
        
        # position
        if 'update' in data['position']:
            x = data['position']['x']
            y = data['position']['y']
            z = data['position']['z']
            self._display.SetSurfacePosition(id, x, y, z)
            data['position'].pop('update')
        data['draw']['all'] = False
        
    def UpdateVolumeProperties(self, id, data, isosurfId = None):
        """!Update volume (isosurface/slice) map object properties"""
        if 'update' in data['draw']['resolution']:
            if data['draw']['mode']['value'] == 0:
                self._display.SetIsosurfaceRes(id, data['draw']['resolution']['isosurface']['value'])
            else:
                self._display.SetSliceRes(id, data['draw']['resolution']['slice']['value'])                
            data['draw']['resolution'].pop('update')
        
        if 'update' in data['draw']['shading']:
            if data['draw']['mode']['value'] == 0:
                if data['draw']['shading']['isosurface']['value'] < 0: # need to calculate
                    mode = data['draw']['shading']['isosurface']['value'] = \
                        self.nvizDefault.GetDrawMode(shade = data['draw']['shading']['isosurface'],
                                                     string = False)
                    self._display.SetIsosurfaceMode(id, mode)
            else:
                if data['draw']['shading']['slice']['value'] < 0: # need to calculate
                    mode = data['draw']['shading']['slice']['value'] = \
                        self.nvizDefault.GetDrawMode(shade = data['draw']['shading']['slice'],
                                                     string = False)
                    self._display.SetSliceMode(id, mode)
            data['draw']['shading'].pop('update')
        
        #
        # isosurface attributes
        #
        isosurfId = 0
        for isosurf in data['isosurface']:
            self._display.AddIsosurface(id, 0, isosurf_id = isosurfId)
            for attrb in ('topo', 'color', 'mask',
                          'transp', 'shine'):
                if attrb not in isosurf or \
                        'update' not in isosurf[attrb]:
                    continue
                map = isosurf[attrb]['map']
                value = isosurf[attrb]['value']
                
                if map is None: # unset
                    # only optional attributes
                    if attrb == 'topo' :
                        self._display.SetIsosurfaceTopo(id, isosurfId, map, str(value))
                    elif attrb ==  'mask':
                        # TODO: invert mask
                        # TODO: broken in NVIZ
                        self._display.UnsetIsosurfaceMask(id, isosurfId)
                    elif attrb ==  'transp':
                        self._display.UnsetIsosurfaceTransp(id, isosurfId) 
                else:
                    if type(value) == types.StringType and \
                            len(value) <=  0: # ignore empty values (TODO: warning)
                        continue
                    elif attrb ==  'color':
                        self._display.SetIsosurfaceColor(id, isosurfId, map, str(value))
                    elif attrb ==  'mask':
                        # TODO: invert mask
                        # TODO: broken in NVIZ
                        self._display.SetIsosurfaceMask(id, isosurfId, False, str(value))
                    elif attrb ==  'transp':
                        self._display.SetIsosurfaceTransp(id, isosurfId, map, str(value)) 
                    elif attrb ==  'shine':
                        self._display.SetIsosurfaceShine(id, isosurfId, map, str(value))  
                isosurf[attrb].pop('update')
            isosurfId +=  1
        #
        # slice attributes
        #
        sliceId = 0
        for slice in data['slice']:
            ret = self._display.AddSlice(id, slice_id = sliceId)
            if 'update' in slice['position']:
                pos = slice['position']
                ret = self._display.SetSlicePosition(id, sliceId, pos['x1'], pos['x2'],
                                               pos['y1'], pos['y2'], pos['z1'], pos['z2'], pos['axis'])
                
                slice['position'].pop('update')
            if 'update' in slice['transp']:
                tr = slice['transp']['value']
                self._display.SetSliceTransp(id, sliceId, tr)
            sliceId += 1
                
        # position
        if 'update' in data['position']:
            x = data['position']['x']
            y = data['position']['y']
            z = data['position']['z']
            self._display.SetVolumePosition(id, x, y, z)
            data['position'].pop('update')
            
    def UpdateVectorProperties(self, id, data, type):
        """!Update vector layer properties
        
        @param id layer id
        @param data properties
        @param type lines/points
        """
        if type ==  'points':
            self.UpdateVectorPointsProperties(id, data[type])
        else:
            self.UpdateVectorLinesProperties(id, data[type])
        
    def UpdateVectorLinesProperties(self, id, data):
        """!Update vector line map object properties"""
        # mode
        if 'update' in data['color'] or \
                'update' in data['width'] or \
                'update' in data['mode']:
            width = data['width']['value']
            color = data['color']['value']
            if data['mode']['type'] ==  'flat':
                flat = True
                if 'surface' in data['mode']:
                    data['mode'].pop('surface')
            else:
                flat = False
            
            self._display.SetVectorLineMode(id, color,
                                            width, flat)
            
            if 'update' in data['color']:
                data['color'].pop('update')
            if 'update' in data['width']:
                data['width'].pop('update')
        
        # height
        if 'update' in data['height']:
            self._display.SetVectorLineHeight(id,
                                              data['height']['value'])
            data['height'].pop('update')
            
        # thematic
        if 'update' in data['thematic']:
            color = width = None
            colorTable = False
            if data['thematic']['usecolor'] or data['thematic']['usewidth']:
                if data['thematic']['usecolor']:
                    color = data['thematic']['rgbcolumn']
                    colorTable = True
                if data['thematic']['usewidth']:
                    width = data['thematic']['sizecolumn']
                self._display.SetLinesStyleThematic(id = id, layer = data['thematic']['layer'],
                                                     color = color,
                                                     colorTable = colorTable, 
                                                     width = width)
            else:
                self._display.UnsetLinesStyleThematic(id = id)
            data['thematic'].pop('update')
        # surface
        if 'surface' in data['mode'] and 'update' in data['mode']:
            for item in range(len(data['mode']['surface']['value'])):
                for type in ('raster', 'constant'):
                    sid = self.GetLayerId(type = type,
                                          name = data['mode']['surface']['value'][item])
                    if sid > -1:
                        if data['mode']['surface']['show'][item]:
                            self._display.SetVectorLineSurface(id, sid)
                        else:
                            self._display.UnsetVectorLineSurface(id, sid)
                        break
                
        if 'update' in data['mode']:
                data['mode'].pop('update')
        
    def UpdateVectorPointsProperties(self, id, data):
        """!Update vector point map object properties"""
        if 'update' in data['size'] or \
                'update' in data['width'] or \
                'update' in data['marker'] or \
                'update' in data['color']:
                
            ret = self._display.SetVectorPointMode(id, data['color']['value'],
                                                   data['width']['value'], float(data['size']['value']),
                                                   data['marker']['value'] + 1)
            
            error = None
            if ret ==  -1:
                error = _("Vector point layer not found (id = %d)") % id
            elif ret ==  -2:
                error = _("Unable to set data layer properties (id = %d)") % id

            if error:
                raise gcmd.GException(_("Setting data layer properties failed.\n\n%s") % error)
            
            for prop in ('size', 'width', 'marker', 'color'):
                if 'update' in data[prop]:
                    data[prop].pop('update')
        
        # height
        if 'update' in data['height']:
            self._display.SetVectorPointHeight(id,
                                               data['height']['value'])
            data['height'].pop('update')
        
        # thematic
        if 'update' in data['thematic']:
            color = size = None
            colorTable = False
            if data['thematic']['usecolor'] or data['thematic']['usesize']:
                if data['thematic']['usecolor']:
                    color = data['thematic']['rgbcolumn']
                    colorTable = True
                if data['thematic']['usesize']:
                    size = data['thematic']['sizecolumn']
                self._display.SetPointsStyleThematic(id = id, layer = data['thematic']['layer'],
                                                     color = color,
                                                     colorTable = colorTable, 
                                                     size = size)
            else:
                self._display.UnsetPointsStyleThematic(id = id)
            data['thematic'].pop('update')
            
        # surface
        if 'update' in data['mode'] and 'surface' in data['mode']:
            for item in range(len(data['mode']['surface']['value'])):
                for type in ('raster', 'constant'):
                    sid = self.GetLayerId(type = type,
                                          name = data['mode']['surface']['value'][item])
                    if sid > -1:
                        if data['mode']['surface']['show'][item]:
                            self._display.SetVectorPointSurface(id, sid)
                        else:
                            self._display.UnsetVectorPointSurface(id, sid)   
                        break
            data['mode'].pop('update')
            
    def GetLayerNames(self, type):
        """!Return list of map layer names of given type"""
        layerName = []
        
        if type == 'constant':
            for item in self.constants:
                layerName.append(_("constant#") + str(item['constant']['object']['name']))
        else:    
            for item in self.layers:
                mapLayer = self.tree.GetPyData(item)[0]['maplayer']
                if type !=  mapLayer.GetType():
                    continue
                
                layerName.append(mapLayer.GetName())
        
        return layerName
    
    def GetLayerId(self, type, name, vsubtyp = None):
        """!Get layer object id or -1"""
        if len(name) < 1:
            return -1
        
        if type == 'constant':
            for item in self.constants:
                if _("constant#") + str(item['constant']['object']['name']) == name:
                    return item['constant']['object']['id']
                
        
        for item in self.layers:
            mapLayer = self.tree.GetPyData(item)[0]['maplayer']
            if type !=  mapLayer.GetType() or \
                    name !=  mapLayer.GetName():
                continue
            
            data = self.tree.GetPyData(item)[0]['nviz']
            
            try:
                if type ==  'raster':
                    return data['surface']['object']['id']
                elif type ==  'vector':
                    if vsubtyp == 'vpoint':
                        return data['vector']['points']['object']['id']
                    elif vsubtyp ==  'vline':
                        return data['vector']['lines']['object']['id']
                elif type ==  '3d-raster':
                    return data['volume']['object']['id']
            except KeyError:
                return -1
        return -1
    
    def ReloadLayersData(self):
        """!Delete nviz data of all loaded layers and reload them from current settings"""
        for item in self.layers:
            type = self.tree.GetPyData(item)[0]['type']
            layer = self.tree.GetPyData(item)[0]['maplayer']
            data = self.tree.GetPyData(item)[0]['nviz']
            
            if type == 'raster':
                self.nvizDefault.SetSurfaceDefaultProp(data['surface'])
            if type == 'vector':
                npoints, nlines, nfeatures, mapIs3D = self.lmgr.nviz.VectorInfo(layer)
                if npoints > 0:
                    self.nvizDefault.SetVectorPointsDefaultProp(data['vector']['points'])
                if nlines > 0:
                    self.nvizDefault.SetVectorLinesDefaultProp(data['vector']['lines'])
            
    def NvizCmdCommand(self):
        """!Generate command for m.nviz.image according to current state"""
        cmd = 'm.nviz.image '
        
        rasters = []
        vectors = []
        volumes = []
        for item in self.layers:
            if self.tree.GetPyData(item)[0]['type'] == 'raster':
                rasters.append(item)
            elif self.tree.GetPyData(item)[0]['type'] == '3d-raster':
                volumes.append(item)
            elif self.tree.GetPyData(item)[0]['type'] == 'vector':
                vectors.append(item)
        if not rasters and not self.constants:
            return _("At least one raster map required")
        # elevation_map/elevation_value
        if self.constants:
            subcmd = "elevation_value="
            for constant in self.constants:
                subcmd += "%d," % constant['constant']['value']
            subcmd = subcmd.strip(', ') + ' '
            cmd += subcmd
        if rasters:
            subcmd = "elevation_map="
            for item in rasters:
                subcmd += "%s," % self.tree.GetPyData(item)[0]['maplayer'].GetName()
            subcmd = subcmd.strip(', ') + ' '
            cmd += subcmd
            #
            # draw mode
            #
            cmdMode = "mode="
            cmdFine = "resolution_fine="
            cmdCoarse = "resolution_coarse="
            cmdShading = "shading="
            cmdStyle = "style="
            cmdWire = "wire_color="
            # test -a flag
            flag_a = "-a "
            nvizDataFirst = self.tree.GetPyData(rasters[0])[0]['nviz']['surface']['draw']
            for item in rasters:
                nvizData = self.tree.GetPyData(item)[0]['nviz']['surface']['draw']
                if nvizDataFirst != nvizData:
                    flag_a = ""
            cmd += flag_a
            for item in rasters:
                nvizData = self.tree.GetPyData(item)[0]['nviz']['surface']['draw']
                
                cmdMode += "%s," % nvizData['mode']['desc']['mode']
                cmdFine += "%s," % nvizData['resolution']['fine']
                cmdCoarse += "%s," % nvizData['resolution']['coarse']
                cmdShading += "%s," % nvizData['mode']['desc']['shading']
                cmdStyle += "%s," % nvizData['mode']['desc']['style']
                cmdWire += "%s," % nvizData['wire-color']['value']
            for item in self.constants:
                cmdMode += "fine,"
                cmdFine += "%s," % item['constant']['resolution']
                cmdCoarse += "%s," % item['constant']['resolution']
                cmdShading += "gouraud,"
                cmdStyle += "surface,"
                cmdWire += "0:0:0,"
            mode = []
            for subcmd in (cmdMode, cmdFine, cmdCoarse, cmdShading, cmdStyle, cmdWire):
                if flag_a:
                    mode.append(subcmd.split(',')[0] + ' ')
                else:
                    subcmd = subcmd.strip(', ') + ' '
                    cmd += subcmd
            if flag_a:# write only meaningful possibilities
                cmd += mode[0]
                if 'fine' in mode[0]:
                    cmd += mode[1]
                elif 'coarse' in mode[0]:
                    cmd += mode[2]            
                elif 'both' in mode[0]:
                    cmd += mode[2]
                    cmd += mode[1]
                if 'flat' in mode[3]:
                    cmd += mode[3]
                if 'wire' in mode[4]:
                    cmd += mode[4]
                if 'coarse' in mode[0] or 'both' in mode[0] and 'wire' in mode[3]:
                    cmd += mode[5]
            #
            # attributes
            #
            cmdColorMap = "color_map="
            cmdColorVal = "color="
            for item in rasters:
                nvizData = self.tree.GetPyData(item)[0]['nviz']['surface']['attribute']
                if 'color' not in nvizData:
                    cmdColorMap += "%s," % self.tree.GetPyData(item)[0]['maplayer'].GetName()
                else:
                    if nvizData['color']['map']:
                        cmdColorMap += "%s," % nvizData['color']['value']
                    else:
                        cmdColorVal += "%s," % nvizData['color']['value']
                        #TODO
                        # transparency, shine, mask
            for item in self.constants:
                cmdColorVal += "%s," % item['constant']['color']
            if cmdColorMap.split("=")[1]:
                cmd += cmdColorMap.strip(', ') + ' '
            if cmdColorVal.split("=")[1]:
                cmd += cmdColorVal.strip(', ') + ' '
            cmd += "\\\n"
        #
        # vlines
        #
        if vectors:
            cmdLines = cmdLWidth = cmdLHeight = cmdLColor = cmdLMode = cmdLPos = \
            cmdPoints = cmdPWidth = cmdPSize = cmdPColor = cmdPMarker = cmdPPos = cmdPLayer = ""
            markers = ['x', 'box', 'sphere', 'cube', 'diamond',
                       'dec_tree', 'con_tree', 'aster', 'gyro', 'histogram']
            for vector in vectors:
                npoints, nlines, nfeatures, mapIs3D = self.lmgr.nviz.VectorInfo(
                                                      self.tree.GetPyData(vector)[0]['maplayer'])
                nvizData = self.tree.GetPyData(vector)[0]['nviz']['vector']
                if nlines > 0:
                    cmdLines += "%s," % self.tree.GetPyData(vector)[0]['maplayer'].GetName()
                    cmdLWidth += "%d," % nvizData['lines']['width']['value']
                    cmdLHeight += "%d," % nvizData['lines']['height']['value']
                    cmdLColor += "%s," % nvizData['lines']['color']['value']
                    cmdLMode += "%s," % nvizData['lines']['mode']['type']
                    cmdLPos += "0,0,%d," % nvizData['lines']['height']['value']
                if npoints > 0:    
                    cmdPoints += "%s," % self.tree.GetPyData(vector)[0]['maplayer'].GetName()
                    cmdPWidth += "%d," % nvizData['points']['width']['value']
                    cmdPSize += "%d," % nvizData['points']['size']['value']
                    cmdPColor += "%s," % nvizData['points']['color']['value']
                    cmdPMarker += "%s," % markers[nvizData['points']['marker']['value']]
                    cmdPPos += "0,0,%d," % nvizData['points']['height']['value']
                    cmdPLayer += "1,1,"
            if cmdLines:
                cmd += "vline=" + cmdLines.strip(',') + ' '
                cmd += "vline_width=" + cmdLWidth.strip(',') + ' '
                cmd += "vline_color=" + cmdLColor.strip(',') + ' '
                cmd += "vline_height=" + cmdLHeight.strip(',') + ' '
                cmd += "vline_mode=" + cmdLMode.strip(',') + ' '
                cmd += "vline_position=" + cmdLPos.strip(',') + ' '
            if cmdPoints:
                cmd += "vpoint=" + cmdPoints.strip(',') + ' '
                cmd += "vpoint_width=" + cmdPWidth.strip(',') + ' '
                cmd += "vpoint_color=" + cmdPColor.strip(',') + ' '
                cmd += "vpoint_size=" + cmdPSize.strip(',') + ' '
                cmd += "vpoint_marker=" + cmdPMarker.strip(',') + ' '
                cmd += "vpoint_position=" + cmdPPos.strip(',') + ' '
                cmd += "vpoint_layer=" + cmdPLayer.strip(',') + ' '
            cmd += "\\\n"
            
        #
        # volumes
        #
        if volumes:
            cmdName = cmdShade = cmdRes = cmdPos = cmdIso = ""
            cmdIsoColorMap = cmdIsoColorVal = cmdIsoTrMap = cmdIsoTrVal = ""
            cmdSlice = cmdSliceTransp = cmdSlicePos = ""
            for i, volume in enumerate(volumes):
                nvizData = self.tree.GetPyData(volume)[0]['nviz']['volume']
                cmdName += "%s," % self.tree.GetPyData(volume)[0]['maplayer'].GetName()
                cmdShade += "%s," % nvizData['draw']['shading']['isosurface']['desc']
                cmdRes += "%d," % nvizData['draw']['resolution']['isosurface']['value']
                if nvizData['position']:
                    cmdPos += "%d,%d,%d," % (nvizData['position']['x'], nvizData['position']['y'],
                                            nvizData['position']['z'])
                for iso in nvizData['isosurface']:
                    level = iso['topo']['value']
                    cmdIso += "%d:%s," % (i + 1, level)
                    if iso['color']['map']:
                        cmdIsoColorMap += "%s," % iso['color']['value']
                    else:
                        cmdIsoColorVal += "%s," % iso['color']['value']
                    if 'transp' in iso:
                        if iso['transp']['map']:
                            cmdIsoTrMap += "%s," % iso['transp']['value']
                        else:
                            cmdIsoTrVal += "%s," % iso['transp']['value']     
                            
                for slice in nvizData['slice']:
                    axis = ('x','y','z')[slice['position']['axis']]
                    cmdSlice += "%d:%s," % (i + 1, axis)
                    for coord in ('x1', 'x2', 'y1', 'y2', 'z1', 'z2'):
                        cmdSlicePos += "%f," % slice['position'][coord]
                    cmdSliceTransp += "%s," % slice['transp']['value']
                        
            cmd += "volume=" + cmdName.strip(',') + ' '
            cmd += "volume_shading=" + cmdShade.strip(',') + ' '
            cmd += "volume_resolution=" + cmdRes.strip(',') + ' '
            if nvizData['position']:
                cmd += "volume_position=" + cmdPos.strip(',') + ' '
            if cmdIso:
                cmd += "isosurf_level=" + cmdIso.strip(',') + ' '
                if cmdIsoColorMap:
                    cmd += "isosurf_color_map=" + cmdIsoColorMap.strip(',') + ' '
                if cmdIsoColorVal:
                    cmd += "isosurf_color_value=" + cmdIsoColorVal.strip(',') + ' ' 
                if cmdIsoTrMap:
                    cmd += "isosurf_transp_map=" + cmdIsoTrMap.strip(',') + ' '
                if cmdIsoTrVal:
                    cmd += "isosurf_transp_value=" + cmdIsoTrVal.strip(',') + ' '
            if cmdSlice:
                cmd += "slice=" + cmdSlice.strip(',') + ' '
                cmd += "slice_position=" + cmdSlicePos.strip(',') + ' '
                cmd += "slice_transparency=" + cmdSliceTransp.strip(',') + ' '
                
        #
        # cutting planes
        #
        cplane = self.lmgr.nviz.FindWindowById(self.lmgr.nviz.win['cplane']['planes']).GetStringSelection()
        try:
            planeIndex = int(cplane.split()[-1]) - 1
        except (IndexError, ValueError):
            planeIndex = None
        if planeIndex is not None:
            shading = ['clear', 'top', 'bottom', 'blend', 'shaded']
            cmd += "cplane=%d " % planeIndex
            cmd += "cplane_rotation=%d " % self.cplanes[planeIndex]['rotation']['rot']
            cmd += "cplane_tilt=%d " % self.cplanes[planeIndex]['rotation']['tilt']
            cmd += "cplane_position=%d,%d,%d " % (self.cplanes[planeIndex]['position']['x'],
                                           self.cplanes[planeIndex]['position']['y'],
                                           self.cplanes[planeIndex]['position']['z'])
            cmd += "cplane_shading=%s " % shading[self.cplanes[planeIndex]['shading']]
            cmd += "\\\n"                                        
        # 
        # viewpoint
        #
        subcmd  = "position=%.2f,%.2f " % (self.view['position']['x'], self.view['position']['y'])
        subcmd += "height=%d " % (self.iview['height']['value'])
        subcmd += "perspective=%d " % (self.view['persp']['value'])
        subcmd += "twist=%d " % (self.view['twist']['value'])
        subcmd += "zexag=%d " % (self.view['z-exag']['value'] * self.iview['z-exag']['original'])
        subcmd += "focus=%d,%d,%d " % (self.iview['focus']['x'],self.iview['focus']['y'],self.iview['focus']['z'])
        cmd += subcmd
        
        # background
        subcmd  = "bgcolor=%d:%d:%d " % (self.view['background']['color'][:3])
        if self.view['background']['color'] != (255, 255, 255):
            cmd += subcmd
        cmd += "\\\n"
        # light
        subcmd  = "light_position=%.2f,%.2f,%.2f " % (self.light['position']['x'],
                                                      self.light['position']['y'],
                                                      self.light['position']['z']/100.)
        subcmd += "light_brightness=%d " % (self.light['bright'])
        subcmd += "light_ambient=%d " % (self.light['ambient'])
        subcmd += "light_color=%d:%d:%d " % (self.light['color'][:3])
        cmd += subcmd
        cmd += "\\\n"
        # fringe
        toolWindow = self.lmgr.nviz
        direction = ''
        for dir in ('nw', 'ne', 'sw', 'se'):
            if toolWindow.FindWindowById(toolWindow.win['fringe'][dir]).IsChecked():
                direction += "%s," % dir
        if direction:
            subcmd = "fringe=%s " % (direction.strip(','))
            color = toolWindow.FindWindowById(toolWindow.win['fringe']['color']).GetValue()
            subcmd += "fringe_color=%d:%d:%d " % (color[0], color[1], color[2])
            subcmd += "fringe_elevation=%d " % (toolWindow.FindWindowById(toolWindow.win['fringe']['elev']).GetValue())
            cmd += subcmd
            cmd += "\\\n"
        # north arrow
        if self.decoration['arrow']['show']:
            subcmd = "arrow_position=%d,%d " % (self.decoration['arrow']['position']['x'],
                                                self.decoration['arrow']['position']['y'])
            subcmd += "arrow_color=%s " % self.decoration['arrow']['color']
            subcmd += "arrow_size=%d " % self.decoration['arrow']['size']
            cmd += subcmd
            
        # output
        subcmd = 'output=nviz_output '
        subcmd += 'format=ppm '
        subcmd += 'size=%d,%d ' % self.GetClientSizeTuple()
        cmd += subcmd
        
        return cmd
    
    def OnNvizCmd(self):
        """!Generate and write command to command output"""
        self.log.WriteLog(self.NvizCmdCommand(), switchPage = True)
        
    def SaveToFile(self, FileName, FileType, width, height):
        """!This draws the DC to a buffer that can be saved to a file.
        
        @todo fix BufferedPaintDC
        
        @param FileName file name
        @param FileType type of bitmap
        @param width image width
        @param height image height
        """
        self._display.SaveToFile(FileName, width, height, FileType)
                
        # pbuffer = wx.EmptyBitmap(max(1, self.Map.width), max(1, self.Map.height))
        # dc = wx.BufferedPaintDC(self, pbuffer)
        # dc.Clear()
        # self.SetCurrent()
        # self._display.Draw(False, -1)
        # pbuffer.SaveFile(FileName, FileType)
        # self.SwapBuffers()
        
    def GetDisplay(self):
        """!Get display instance"""
        return self._display
        
    def ZoomToMap(self):
        """!Reset view
        """
        self.lmgr.nviz.OnResetView(None)
        
    def TextBounds(self, textinfo):
        """!Return text boundary data
        
        @param textinfo text metadata (text, font, color, rotation)
        """
        return self.parent.MapWindow2D.TextBounds(textinfo, relcoords = True)
