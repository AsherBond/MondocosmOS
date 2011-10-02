"""!
@package layertree.py

@brief Utility classes for map layer management.

Classes:
 - AbstractLayer
 - Layer
 - LayerTree

(C) 2007-2011 by the GRASS Development Team
This program is free software under the GNU General Public
License (>=v2). Read the file COPYING that comes with GRASS
for details.
 
@author Michael Barton (Arizona State University)
@author Jachym Cepicky (Mendel University of Agriculture)
@author Martin Landa <landa.martin gmail.com>
"""

import os
import sys
import string

import wx
try:
    import wx.lib.agw.customtreectrl as CT
except ImportError:
    import wx.lib.customtreectrl as CT
import wx.combo
import wx.lib.newevent
import wx.lib.buttons  as  buttons
try:
    import treemixin 
except ImportError:
    from wx.lib.mixins import treemixin

import globalvar

from grass.script import core as grass

import gdialogs
import menuform
import toolbars
import mapdisp
import render
import histogram
import utils
from wxplot      import ProfileFrame
from debug       import Debug
from icon        import Icons
from preferences import globalSettings as UserSettings
from vdigit      import haveVDigit
from gcmd        import GWarning

TREE_ITEM_HEIGHT = 25

class LayerTree(treemixin.DragAndDrop, CT.CustomTreeCtrl):
    """!Creates layer tree structure
    """
    def __init__(self, parent,
                 id = wx.ID_ANY, style = wx.SUNKEN_BORDER,
                 ctstyle = CT.TR_HAS_BUTTONS | CT.TR_HAS_VARIABLE_ROW_HEIGHT |
                 CT.TR_HIDE_ROOT | CT.TR_ROW_LINES | CT.TR_FULL_ROW_HIGHLIGHT |
                 CT.TR_MULTIPLE, **kwargs):
        
        if 'style' in kwargs:
            ctstyle |= kwargs['style']
            del kwargs['style']
        self.disp_idx = kwargs['idx']
        del kwargs['idx']
        self.lmgr = kwargs['lmgr']
        del kwargs['lmgr']
        self.notebook = kwargs['notebook']   # GIS Manager notebook for layer tree
        del kwargs['notebook']
        self.auimgr = kwargs['auimgr']       # aui manager
        del kwargs['auimgr']
        showMapDisplay = kwargs['showMapDisplay']
        del kwargs['showMapDisplay']
        self.treepg = parent                 # notebook page holding layer tree
        self.Map = render.Map()              # instance of render.Map to be associated with display
        self.root = None                     # ID of layer tree root node
        self.groupnode = 0                   # index value for layers
        self.optpage = {}                    # dictionary of notebook option pages for each map layer
        self.layer_selected = None           # ID of currently selected layer
        self.saveitem = {}                   # dictionary to preserve layer attributes for drag and drop
        self.first = True                    # indicates if a layer is just added or not
        self.flag = ''                       # flag for drag and drop hittest
        self.rerender = False                # layer change requires a rerendering if auto render
        self.reorder = False                 # layer change requires a reordering
        
        try:
            ctstyle |= CT.TR_ALIGN_WINDOWS
        except AttributeError:
            pass
        
        if globalvar.hasAgw:
            super(LayerTree, self).__init__(parent, id, agwStyle = ctstyle, **kwargs)
        else:
            super(LayerTree, self).__init__(parent, id, style = ctstyle, **kwargs)
        self.SetName("LayerTree")
        
        ### SetAutoLayout() causes that no vertical scrollbar is displayed
        ### when some layers are not visible in layer tree
        # self.SetAutoLayout(True)
        self.SetGradientStyle(1)
        self.EnableSelectionGradient(True)
        self._setGradient()
        
        # init associated map display
        pos = wx.Point((self.disp_idx + 1) * 25, (self.disp_idx + 1) * 25)
        self.mapdisplay = mapdisp.MapFrame(self,
                                           id = wx.ID_ANY, pos = pos,
                                           size = globalvar.MAP_WINDOW_SIZE,
                                           style = wx.DEFAULT_FRAME_STYLE,
                                           tree = self, notebook = self.notebook,
                                           lmgr = self.lmgr, page = self.treepg,
                                           Map = self.Map, auimgr = self.auimgr)
        
        # title
        self.mapdisplay.SetTitle(_("GRASS GIS Map Display: %(id)d  - Location: %(loc)s") % \
                                     { 'id' : self.disp_idx + 1,
                                       'loc' : grass.gisenv()["LOCATION_NAME"] })
        
        # show new display
        if showMapDisplay is True:
            self.mapdisplay.Show()
            self.mapdisplay.Refresh()
            self.mapdisplay.Update()
        
        self.root = self.AddRoot(_("Map Layers"))
        self.SetPyData(self.root, (None, None))
        
        #create image list to use with layer tree
        il = wx.ImageList(16, 16, mask = False)
        
        trart = wx.ArtProvider.GetBitmap(wx.ART_FOLDER_OPEN, wx.ART_OTHER, (16, 16))
        self.folder_open = il.Add(trart)
        trart = wx.ArtProvider.GetBitmap(wx.ART_FOLDER, wx.ART_OTHER, (16, 16))
        self.folder = il.Add(trart)
        
        bmpsize = (16, 16)
        icons = Icons['layerManager']
        trgif = icons["addRast"].GetBitmap(bmpsize)
        self.rast_icon = il.Add(trgif)
        
        trgif = icons["addRast3d"].GetBitmap(bmpsize)
        self.rast3d_icon = il.Add(trgif)
        
        trgif = icons["addRgb"].GetBitmap(bmpsize)
        self.rgb_icon = il.Add(trgif)
        
        trgif = icons["addHis"].GetBitmap(bmpsize)
        self.his_icon = il.Add(trgif)
        
        trgif = icons["addShaded"].GetBitmap(bmpsize)
        self.shaded_icon = il.Add(trgif)
        
        trgif = icons["addRArrow"].GetBitmap(bmpsize)
        self.rarrow_icon = il.Add(trgif)
        
        trgif = icons["addRNum"].GetBitmap(bmpsize)
        self.rnum_icon = il.Add(trgif)
        
        trgif = icons["addVect"].GetBitmap(bmpsize)
        self.vect_icon = il.Add(trgif)
        
        trgif = icons["addThematic"].GetBitmap(bmpsize)
        self.theme_icon = il.Add(trgif)
        
        trgif = icons["addChart"].GetBitmap(bmpsize)
        self.chart_icon = il.Add(trgif)
        
        trgif = icons["addGrid"].GetBitmap(bmpsize)
        self.grid_icon = il.Add(trgif)
        
        trgif = icons["addGeodesic"].GetBitmap(bmpsize)
        self.geodesic_icon = il.Add(trgif)
        
        trgif = icons["addRhumb"].GetBitmap(bmpsize)
        self.rhumb_icon = il.Add(trgif)
        
        trgif = icons["addLabels"].GetBitmap(bmpsize)
        self.labels_icon = il.Add(trgif)
        
        trgif = icons["addCmd"].GetBitmap(bmpsize)
        self.cmd_icon = il.Add(trgif)
        
        self.AssignImageList(il)
        
        self.Bind(wx.EVT_TREE_ITEM_EXPANDING,   self.OnExpandNode)
        self.Bind(wx.EVT_TREE_ITEM_COLLAPSED,   self.OnCollapseNode)
        self.Bind(wx.EVT_TREE_ITEM_ACTIVATED,   self.OnActivateLayer)
        self.Bind(wx.EVT_TREE_SEL_CHANGED,      self.OnChangeSel)
        self.Bind(CT.EVT_TREE_ITEM_CHECKED,     self.OnLayerChecked)
        self.Bind(wx.EVT_TREE_DELETE_ITEM,      self.OnDeleteLayer)
        self.Bind(wx.EVT_TREE_ITEM_RIGHT_CLICK, self.OnLayerContextMenu)
        self.Bind(wx.EVT_TREE_END_DRAG,         self.OnEndDrag)
        self.Bind(wx.EVT_TREE_END_LABEL_EDIT,   self.OnRenamed)
        self.Bind(wx.EVT_KEY_UP,                self.OnKeyUp)
        self.Bind(wx.EVT_IDLE,                  self.OnIdle)

    def _setGradient(self, iType = None):
        """!Set gradient for items

        @param iType bgmap, vdigit or None
        """
        if iType == 'bgmap':
            self.SetFirstGradientColour(wx.Colour(0, 100, 0))
            self.SetSecondGradientColour(wx.Colour(0, 150, 0))
        elif iType == 'vdigit':
            self.SetFirstGradientColour(wx.Colour(100, 0, 0))
            self.SetSecondGradientColour(wx.Colour(150, 0, 0))
        else:
            self.SetFirstGradientColour(wx.Colour(100, 100, 100))
            self.SetSecondGradientColour(wx.Colour(150, 150, 150))
        
    def GetMap(self):
        """!Get map instace"""
        return self.Map
    
    def GetMapDisplay(self):
        """!Get associated MapFrame"""
        return self.mapdisplay
    
    def OnIdle(self, event):
        """!Only re-order and re-render a composite map image from GRASS during
        idle time instead of multiple times during layer changing.
        """
        if self.rerender:
            if self.mapdisplay.toolbars['vdigit']:
                vector = True
            else:
                vector = False
            if self.mapdisplay.statusbarWin['render'].GetValue():
                self.mapdisplay.MapWindow.UpdateMap(render = True, renderVector = vector)
                if self.lmgr.IsPaneShown('toolbarNviz'): # nviz
                    self.mapdisplay.MapWindow3D.UpdateMap(render = True)
            self.rerender = False
        
        event.Skip()
        
    def OnKeyUp(self, event):
        """!Key pressed"""
        key = event.GetKeyCode()
        
        if key == wx.WXK_DELETE and self.lmgr and \
                not self.GetEditControl():
            self.lmgr.OnDeleteLayer(None)
        
        event.Skip()
        
    def OnLayerContextMenu (self, event):
        """!Contextual menu for item/layer"""
        if not self.layer_selected:
            event.Skip()
            return

        ltype = self.GetPyData(self.layer_selected)[0]['type']
        mapLayer = self.GetPyData(self.layer_selected)[0]['maplayer']
        
        Debug.msg (4, "LayerTree.OnContextMenu: layertype=%s" % \
                       ltype)

        if not hasattr (self, "popupID"):
            self.popupID = dict()
            for key in ('remove', 'rename', 'opacity', 'nviz', 'zoom',
                        'region', 'export', 'attr', 'edit0', 'edit1',
                        'bgmap', 'topo', 'meta', 'null', 'zoom1', 'region1',
                        'color', 'hist', 'univar', 'prof', 'properties', 'sql'):
                self.popupID[key] = wx.NewId()
        
        self.popupMenu = wx.Menu()
        
        numSelected = len(self.GetSelections())
        
        self.popupMenu.Append(self.popupID['remove'], text = _("Remove"))
        self.Bind(wx.EVT_MENU, self.lmgr.OnDeleteLayer, id = self.popupID['remove'])
        
        if ltype != "command":
            self.popupMenu.Append(self.popupID['rename'], text = _("Rename"))
            self.Bind(wx.EVT_MENU, self.OnRenameLayer, id = self.popupID['rename'])
            if numSelected > 1:
                self.popupMenu.Enable(self.popupID['rename'], False)
        
        # map layer items
        if ltype not in ("group", "command"):
            self.popupMenu.AppendSeparator()
            self.popupMenu.Append(self.popupID['opacity'], text = _("Change opacity level"))
            self.Bind(wx.EVT_MENU, self.OnPopupOpacityLevel, id = self.popupID['opacity'])
            self.popupMenu.Append(self.popupID['properties'], text = _("Properties"))
            self.Bind(wx.EVT_MENU, self.OnPopupProperties, id = self.popupID['properties'])
            
            if numSelected > 1:
                self.popupMenu.Enable(self.popupID['opacity'], False)
                self.popupMenu.Enable(self.popupID['properties'], False)
            
            if ltype in ('raster', 'vector', '3d-raster') and self.lmgr.IsPaneShown('toolbarNviz'):
                self.popupMenu.Append(self.popupID['nviz'], _("3D view properties"))
                self.Bind (wx.EVT_MENU, self.OnNvizProperties, id = self.popupID['nviz'])
            
            if ltype in ('raster', 'vector', 'rgb'):
                self.popupMenu.Append(self.popupID['zoom'], text = _("Zoom to selected map(s)"))
                self.Bind(wx.EVT_MENU, self.mapdisplay.OnZoomToMap, id = self.popupID['zoom'])
                self.popupMenu.Append(self.popupID['region'], text = _("Set computational region from selected map(s)"))
                self.Bind(wx.EVT_MENU, self.OnSetCompRegFromMap, id = self.popupID['region'])
        
        # specific items
        try:
            mltype = self.GetPyData(self.layer_selected)[0]['type']
        except:
            mltype = None
        
        # vector layers (specific items)
        if mltype and mltype == "vector":
            self.popupMenu.AppendSeparator()
            self.popupMenu.Append(self.popupID['export'], text = _("Export"))
            self.Bind(wx.EVT_MENU, lambda x: self.lmgr.OnMenuCmd(cmd = ['v.out.ogr',
                                                                        'input=%s' % mapLayer.GetName()]),
                      id = self.popupID['export'])
            
            self.popupMenu.AppendSeparator()

            self.popupMenu.Append(self.popupID['color'], _("Set color table"))
            self.Bind (wx.EVT_MENU, self.OnVectorColorTable, id = self.popupID['color'])

            self.popupMenu.Append(self.popupID['attr'], text = _("Show attribute data"))
            self.Bind(wx.EVT_MENU, self.lmgr.OnShowAttributeTable, id = self.popupID['attr'])

            self.popupMenu.Append(self.popupID['edit0'], text = _("Start editing"))
            self.popupMenu.Append(self.popupID['edit1'], text = _("Stop editing"))
            self.popupMenu.Enable(self.popupID['edit1'], False)
            self.Bind (wx.EVT_MENU, self.OnStartEditing, id = self.popupID['edit0'])
            self.Bind (wx.EVT_MENU, self.OnStopEditing,  id = self.popupID['edit1'])
            
            layer = self.GetPyData(self.layer_selected)[0]['maplayer']
            # enable editing only for vector map layers available in the current mapset
            digitToolbar = self.mapdisplay.toolbars['vdigit']
            if digitToolbar:
                # background vector map
                self.popupMenu.Append(self.popupID['bgmap'],
                                      text = _("Use as background vector map for digitizer"),
                                      kind = wx.ITEM_CHECK)
                self.Bind(wx.EVT_MENU, self.OnSetBgMap, id = self.popupID['bgmap'])
                if UserSettings.Get(group = 'vdigit', key = 'bgmap', subkey = 'value',
                                    internal = True) == layer.GetName():
                    self.popupMenu.Check(self.popupID['bgmap'], True)
            
            self.popupMenu.Append(self.popupID['topo'], text = _("Rebuild topology"))
            self.Bind(wx.EVT_MENU, self.OnTopology, id = self.popupID['topo'])

            # determine format
            if layer and layer.GetType() == 'vector':
                if 'info' not in self.GetPyData(self.layer_selected)[0]:
                    info = grass.parse_command('v.info',
                                               flags = 'g',
                                               map = layer.GetName())
                    self.GetPyData(self.layer_selected)[0]['info'] = info
                info = self.GetPyData(self.layer_selected)[0]['info']
                if info and info['format'] == 'ogr,PostgreSQL':
                    self.popupMenu.Append(self.popupID['sql'], text = _("SQL Spatial Query"))
                    self.Bind(wx.EVT_MENU, self.OnSqlQuery, id = self.popupID['sql'])
            
            if layer.GetMapset() != grass.gisenv()['MAPSET']:
                # only vector map in current mapset can be edited
                self.popupMenu.Enable (self.popupID['edit0'], False)
                self.popupMenu.Enable (self.popupID['edit1'], False)
                self.popupMenu.Enable (self.popupID['topo'], False)
            elif digitToolbar and digitToolbar.GetLayer():
                # vector map already edited
                vdigitLayer = digitToolbar.GetLayer()
                if vdigitLayer is layer:
                    self.popupMenu.Enable(self.popupID['edit0'],  False)
                    self.popupMenu.Enable(self.popupID['edit1'],  True)
                    self.popupMenu.Enable(self.popupID['remove'], False)
                    self.popupMenu.Enable(self.popupID['bgmap'],  False)
                    self.popupMenu.Enable(self.popupID['topo'],   False)
                else:
                    self.popupMenu.Enable(self.popupID['edit0'], False)
                    self.popupMenu.Enable(self.popupID['edit1'], False)
                    self.popupMenu.Enable(self.popupID['bgmap'], True)
            
            self.popupMenu.Append(self.popupID['meta'], _("Metadata"))
            self.Bind (wx.EVT_MENU, self.OnMetadata, id = self.popupID['meta'])
            if numSelected > 1:
                self.popupMenu.Enable(self.popupID['attr'],   False)
                self.popupMenu.Enable(self.popupID['edit0'],  False)
                self.popupMenu.Enable(self.popupID['edit1'],  False)
                self.popupMenu.Enable(self.popupID['meta'],   False)
                self.popupMenu.Enable(self.popupID['bgmap'],  False)
                self.popupMenu.Enable(self.popupID['topo'],   False)
                self.popupMenu.Enable(self.popupID['export'], False)
        
        # raster layers (specific items)
        elif mltype and mltype == "raster":
            self.popupMenu.Append(self.popupID['zoom1'], text = _("Zoom to selected map(s) (ignore NULLs)"))
            self.Bind(wx.EVT_MENU, self.mapdisplay.OnZoomToRaster, id = self.popupID['zoom1'])
            self.popupMenu.Append(self.popupID['region1'], text = _("Set computational region from selected map(s) (ignore NULLs)"))
            self.Bind(wx.EVT_MENU, self.OnSetCompRegFromRaster, id = self.popupID['region1'])
            
            self.popupMenu.AppendSeparator()
            self.popupMenu.Append(self.popupID['export'], text = _("Export"))
            self.Bind(wx.EVT_MENU, lambda x: self.lmgr.OnMenuCmd(cmd = ['r.out.gdal',
                                                                        'input=%s' % mapLayer.GetName()]),
                      id = self.popupID['export'])
            
            self.popupMenu.AppendSeparator()
            self.popupMenu.Append(self.popupID['color'], _("Set color table"))
            self.Bind (wx.EVT_MENU, self.OnRasterColorTable, id = self.popupID['color'])
            self.popupMenu.Append(self.popupID['hist'], _("Histogram"))
            self.Bind (wx.EVT_MENU, self.OnHistogram, id = self.popupID['hist'])
            self.popupMenu.Append(self.popupID['univar'], _("Univariate raster statistics"))
            self.Bind (wx.EVT_MENU, self.OnUnivariateStats, id = self.popupID['univar'])
            self.popupMenu.Append(self.popupID['prof'], _("Profile"))
            self.Bind (wx.EVT_MENU, self.OnProfile, id = self.popupID['prof'])
            self.popupMenu.Append(self.popupID['meta'], _("Metadata"))
            self.Bind (wx.EVT_MENU, self.OnMetadata, id = self.popupID['meta'])
            
            if numSelected > 1:
                self.popupMenu.Enable(self.popupID['zoom1'],   False)
                self.popupMenu.Enable(self.popupID['region1'], False)
                self.popupMenu.Enable(self.popupID['color'],   False)
                self.popupMenu.Enable(self.popupID['hist'],    False)
                self.popupMenu.Enable(self.popupID['univar'],  False)
                self.popupMenu.Enable(self.popupID['prof'],    False)
                self.popupMenu.Enable(self.popupID['meta'],    False)
                self.popupMenu.Enable(self.popupID['nviz'],    False)
                self.popupMenu.Enable(self.popupID['export'],  False)

        self.PopupMenu(self.popupMenu)
        self.popupMenu.Destroy()
        
    def OnTopology(self, event):
        """!Rebuild topology of selected vector map"""
        mapLayer = self.GetPyData(self.layer_selected)[0]['maplayer']
        cmd = ['v.build',
               'map=%s' % mapLayer.GetName()]
        self.lmgr.goutput.RunCmd(cmd, switchPage = True)
        
    def OnSqlQuery(self, event):
        """!Show SQL query window for PostGIS layers
        """
        dlg = gdialogs.SqlQueryFrame(parent = self)
        dlg.CentreOnScreen()
        dlg.Show()
        
    def OnMetadata(self, event):
        """!Print metadata of raster/vector map layer
        TODO: Dialog to modify metadata
        """
        mapLayer = self.GetPyData(self.layer_selected)[0]['maplayer']
        mltype = self.GetPyData(self.layer_selected)[0]['type']

        if mltype == 'raster':
            cmd = ['r.info']
        elif mltype == 'vector':
            cmd = ['v.info']
        cmd.append('map=%s' % mapLayer.GetName())

        # print output to command log area
        self.lmgr.goutput.RunCmd(cmd, switchPage = True)

    def OnSetCompRegFromRaster(self, event):
        """!Set computational region from selected raster map (ignore NULLs)"""
        mapLayer = self.GetPyData(self.layer_selected)[0]['maplayer']
        
        cmd = ['g.region',
               '-p',
               'zoom=%s' % mapLayer.GetName()]
        
        # print output to command log area
        self.lmgr.goutput.RunCmd(cmd)
         
    def OnSetCompRegFromMap(self, event):
        """!Set computational region from selected raster/vector map
        """
        rast = []
        vect = []
        rast3d = []
        for layer in self.GetSelections():
            mapLayer = self.GetPyData(layer)[0]['maplayer']
            mltype = self.GetPyData(layer)[0]['type']
                
            if mltype == 'raster':
                rast.append(mapLayer.GetName())
            elif mltype == 'vector':
                vect.append(mapLayer.GetName())
            elif mltype == '3d-raster':
                rast3d.append(mapLayer.GetName())
            elif mltype == 'rgb':
                for rname in mapLayer.GetName().splitlines():
                    rast.append(rname)
        
        cmd = ['g.region']
        if rast:
            cmd.append('rast=%s' % ','.join(rast))
        if vect:
            cmd.append('vect=%s' % ','.join(vect))
        if rast3d:
            cmd.append('rast3d=%s' % ','.join(rast3d))
        
        # print output to command log area
        if len(cmd) > 1:
            cmd.append('-p')
            self.lmgr.goutput.RunCmd(cmd)
        
    def OnProfile(self, event):
        """!Plot profile of given raster map layer"""
        mapLayer = self.GetPyData(self.layer_selected)[0]['maplayer']
        if not mapLayer.GetName():
            wx.MessageBox(parent = self,
                          message = _("Unable to create profile of "
                                    "raster map."),
                          caption = _("Error"), style = wx.OK | wx.ICON_ERROR | wx.CENTRE)
            return False

        if not hasattr (self, "profileFrame"):
            self.profileFrame = None

        if hasattr (self.mapdisplay, "profile") and self.mapdisplay.profile:
            self.profileFrame = self.mapdisplay.profile

        if not self.profileFrame:
            self.profileFrame = ProfileFrame(parent = self.mapdisplay,
                                             rasterList = [mapLayer.GetName()])
            # show new display
            self.profileFrame.Show()
        
    def OnRasterColorTable(self, event):
        """!Set color table for raster map"""
        name = self.GetPyData(self.layer_selected)[0]['maplayer'].GetName()
        menuform.GUI(parent = self, centreOnParent = False).ParseCommand(['r.colors',
                                                                          'map=%s' % name])

    def OnVectorColorTable(self, event):
        """!Set color table for vector map"""
        name = self.GetPyData(self.layer_selected)[0]['maplayer'].GetName()
        menuform.GUI(parent = self, centreOnParent = False).ParseCommand(['v.colors',
                                                                          'map=%s' % name])
        
    def OnHistogram(self, event):
        """
        Plot histogram for given raster map layer
        """
        mapLayer = self.GetPyData(self.layer_selected)[0]['maplayer']
        if not mapLayer.GetName():
            wx.MessageBox(parent = self,
                          message = _("Unable to display histogram of "
                                    "raster map."),
                          caption = _("Error"), style = wx.OK | wx.ICON_ERROR | wx.CENTRE)
            return False

        if not hasattr (self, "histogramFrame"):
            self.histogramFrame = None

        if hasattr (self.mapdisplay, "histogram") and self.mapdisplay.histogram:
            self.histogramFrame = self.mapdisplay.histogram

        if not self.histogramFrame:
            self.histogramFrame = histogram.HistFrame(self,
                                                      id = wx.ID_ANY,
                                                      pos = wx.DefaultPosition, size = globalvar.HIST_WINDOW_SIZE,
                                                      style = wx.DEFAULT_FRAME_STYLE)
            self.histogramFrame.CentreOnScreen()
            # show new display
            self.histogramFrame.Show()

        self.histogramFrame.SetHistLayer(mapLayer.GetName())
        self.histogramFrame.HistWindow.UpdateHist()
        self.histogramFrame.Refresh()
        self.histogramFrame.Update()

        return True

    def OnUnivariateStats(self, event):
        """!Univariate raster statistics"""
        name = self.GetPyData(self.layer_selected)[0]['maplayer'].GetName()
        menuform.GUI(parent = self).ParseCommand(['r.univar',
                                                  'map=%s' % name])

    def OnStartEditing(self, event):
        """!Start editing vector map layer requested by the user
        """
        try:
            maplayer = self.GetPyData(self.layer_selected)[0]['maplayer']
        except:
            event.Skip()
            return
        
        if not haveVDigit:
            from vdigit import errorMsg
            msg = _("Unable to start wxGUI vector digitizer.\nDo you want to start "
                    "TCL/TK digitizer (v.digit) instead?\n\n"
                    "Details: %s" % errorMsg)
            
            self.mapdisplay.toolbars['map'].combo.SetValue (_("2D view"))
            dlg = wx.MessageDialog(parent = self.mapdisplay,
                                   message = msg,
                                   caption=_("Vector digitizer failed"),
                                   style = wx.YES_NO | wx.CENTRE)
            if dlg.ShowModal() == wx.ID_YES:
                self.lmgr.goutput.RunCmd(['v.digit', 'map=%s' % maplayer.GetName()],
                                         switchPage = False)
            
            dlg.Destroy()
            return
        
        if not self.mapdisplay.toolbars['vdigit']: # enable tool
            self.mapdisplay.AddToolbar('vdigit')
        else: # tool already enabled
            pass
        
        # mark layer as 'edited'
        self.mapdisplay.toolbars['vdigit'].StartEditing(maplayer)
        
        self._setGradient('vdigit')
        self.RefreshLine(self.layer_selected)
        
    def OnStopEditing(self, event):
        """!Stop editing the current vector map layer
        """
        maplayer = self.GetPyData(self.layer_selected)[0]['maplayer']
        
        self.mapdisplay.toolbars['vdigit'].OnExit()
        if self.lmgr:
            self.lmgr.toolbars['tools'].Enable('vdigit', enable = True)
        
        self._setGradient()
        self.RefreshLine(self.layer_selected)
        
    def OnSetBgMap(self, event):
        """!Set background vector map for editing sesstion"""
        digit = self.mapdisplay.GetWindow().digit
        if event.IsChecked():
            mapName = self.GetPyData(self.layer_selected)[0]['maplayer'].GetName()
            UserSettings.Set(group = 'vdigit', key = 'bgmap', subkey = 'value',
                             value = str(mapName), internal = True)
            digit.OpenBackgroundMap(mapName)
            self._setGradient('bgmap')
        else:
            UserSettings.Set(group = 'vdigit', key = 'bgmap', subkey = 'value',
                             value = '', internal = True)
            digit.CloseBackgroundMap()
            self._setGradient()
        
        self.RefreshLine(self.layer_selected)

    def OnPopupProperties (self, event):
        """!Popup properties dialog"""
        self.PropertiesDialog(self.layer_selected)

    def OnPopupOpacityLevel(self, event):
        """!Popup opacity level indicator"""
        if not self.GetPyData(self.layer_selected)[0]['ctrl']:
            return
        
        maplayer = self.GetPyData(self.layer_selected)[0]['maplayer']
        current_opacity = maplayer.GetOpacity()
        
        dlg = gdialogs.SetOpacityDialog(self, opacity = current_opacity,
                                        title = _("Set opacity <%s>") % maplayer.GetName())
        dlg.CentreOnParent()

        if dlg.ShowModal() == wx.ID_OK:
            new_opacity = dlg.GetOpacity() # string
            self.Map.ChangeOpacity(maplayer, new_opacity)
            maplayer.SetOpacity(new_opacity)
            self.SetItemText(self.layer_selected,
                             self._getLayerName(self.layer_selected))
            
            # vector layer currently edited
            if self.mapdisplay.toolbars['vdigit'] and \
                    self.mapdisplay.toolbars['vdigit'].GetLayer() == maplayer:   
                alpha = int(new_opacity * 255)
                self.mapdisplay.GetWindow().digit.GetDisplay().UpdateSettings(alpha = alpha)
                
            # redraw map if auto-rendering is enabled
            self.rerender = True
            self.reorder = True

    def OnNvizProperties(self, event):
        """!Nviz-related properties (raster/vector/volume)

        @todo vector/volume
        """
        self.lmgr.notebook.SetSelectionByName('nviz')
        ltype = self.GetPyData(self.layer_selected)[0]['type']
        if ltype == 'raster':
            self.lmgr.nviz.SetPage('surface')
        elif ltype == 'vector':
            self.lmgr.nviz.SetPage('vector')
        elif ltype == '3d-raster':
            self.lmgr.nviz.SetPage('volume')
        
    def OnRenameLayer (self, event):
        """!Rename layer"""
        self.EditLabel(self.layer_selected)
        self.GetEditControl().SetSelection(-1, -1)
        
    def OnRenamed(self, event):
        """!Layer renamed"""
        if not event.GetLabel():
            event.Skip()
            return
        
        item = self.layer_selected
        self.GetPyData(item)[0]['label'] = event.GetLabel()
        self.SetItemText(item, self._getLayerName(item))
        
        event.Skip()

    def AddLayer(self, ltype, lname = None, lchecked = None,
                 lopacity = 1.0, lcmd = None, lgroup = None, lvdigit = None, lnviz = None, multiple = True):
        """!Add new item to the layer tree, create corresponding MapLayer instance.
        Launch property dialog if needed (raster, vector, etc.)

        @param ltype layer type (raster, vector, 3d-raster, ...)
        @param lname layer name
        @param lchecked if True layer is checked
        @param lopacity layer opacity level
        @param lcmd command (given as a list)
        @param lgroup index of group item (-1 for root) or None
        @param lvdigit vector digitizer settings (eg. geometry attributes)
        @param lnviz layer Nviz properties
        @param multiple True to allow multiple map layers in layer tree
        """
        if lname and not multiple:
            # check for duplicates
            item = self.GetFirstVisibleItem()
            while item and item.IsOk():
                if self.GetPyData(item)[0]['type'] == 'vector':
                    name = self.GetPyData(item)[0]['maplayer'].GetName()
                    if name == lname:
                        return
                item = self.GetNextVisible(item)
        
        self.first = True
        params = {} # no initial options parameters
        
        # deselect active item
        if self.layer_selected:
            self.SelectItem(self.layer_selected, select = False)
        
        Debug.msg (3, "LayerTree().AddLayer(): ltype=%s" % (ltype))
        
        if ltype == 'command':
            # generic command item
            ctrl = wx.TextCtrl(self, id = wx.ID_ANY, value = '',
                               pos = wx.DefaultPosition, size = (self.GetSize()[0]-100,25),
                               # style = wx.TE_MULTILINE|wx.TE_WORDWRAP)
                               style = wx.TE_PROCESS_ENTER | wx.TE_DONTWRAP)
            ctrl.Bind(wx.EVT_TEXT_ENTER, self.OnCmdChanged)
            # ctrl.Bind(wx.EVT_TEXT,       self.OnCmdChanged)
        elif ltype == 'group':
            # group item
            ctrl = None
            grouptext = _('Layer group:') + str(self.groupnode)
            self.groupnode += 1
        else:
            btnbmp = Icons['layerManager']["layerOptions"].GetBitmap((16,16))
            ctrl = buttons.GenBitmapButton(self, id = wx.ID_ANY, bitmap = btnbmp, size = (24,24))
            ctrl.SetToolTipString(_("Click to edit layer settings"))
            self.Bind(wx.EVT_BUTTON, self.OnLayerContextMenu, ctrl)
        # add layer to the layer tree
        if self.layer_selected and self.layer_selected != self.GetRootItem():
            if self.GetPyData(self.layer_selected)[0]['type'] == 'group' \
                and self.IsExpanded(self.layer_selected):
                # add to group (first child of self.layer_selected) if group expanded
                layer = self.PrependItem(parent = self.layer_selected,
                                         text = '', ct_type = 1, wnd = ctrl)
            else:
                # prepend to individual layer or non-expanded group
                if lgroup == -1:
                    # -> last child of root (loading from workspace)
                    layer = self.AppendItem(parentId = self.root,
                                            text = '', ct_type = 1, wnd = ctrl)
                elif lgroup > -1:
                    # -> last child of group (loading from workspace)
                    parent = self.FindItemByIndex(index = lgroup)
                    if not parent:
                        parent = self.root
                    layer = self.AppendItem(parentId = parent,
                                            text = '', ct_type = 1, wnd = ctrl)
                elif lgroup is None:
                    # -> previous sibling of selected layer
                    parent = self.GetItemParent(self.layer_selected)
                    layer = self.InsertItem(parentId = parent,
                                            input = self.GetPrevSibling(self.layer_selected),
                                            text = '', ct_type = 1, wnd = ctrl)
        else: # add first layer to the layer tree (first child of root)
            layer = self.PrependItem(parent = self.root, text = '', ct_type = 1, wnd = ctrl)
        
        # layer is initially unchecked as inactive (beside 'command')
        # use predefined value if given
        if lchecked is not None:
            checked = lchecked
        else:
            checked = True
        
        self.CheckItem(layer, checked = checked)
        
        # add text and icons for each layer ltype
        label =  _('(double click to set properties)') + ' ' * 35
        if ltype == 'raster':
            self.SetItemImage(layer, self.rast_icon)
            self.SetItemText(layer, '%s %s' % (_('raster'), label))
        elif ltype == '3d-raster':
            self.SetItemImage(layer, self.rast3d_icon)
            self.SetItemText(layer, '%s %s' % (_('3d raster'), label))
        elif ltype == 'rgb':
            self.SetItemImage(layer, self.rgb_icon)
            self.SetItemText(layer, '%s %s' % (_('RGB'), label))
        elif ltype == 'his':
            self.SetItemImage(layer, self.his_icon)
            self.SetItemText(layer, '%s %s' % (_('HIS'), label))
        elif ltype == 'shaded':
            self.SetItemImage(layer, self.shaded_icon)
            self.SetItemText(layer, '%s %s' % (_('Shaded relief'), label))
        elif ltype == 'rastnum':
            self.SetItemImage(layer, self.rnum_icon)
            self.SetItemText(layer, '%s %s' % (_('raster cell numbers'), label))
        elif ltype == 'rastarrow':
            self.SetItemImage(layer, self.rarrow_icon)
            self.SetItemText(layer, '%s %s' % (_('raster flow arrows'), label))
        elif ltype == 'vector':
            self.SetItemImage(layer, self.vect_icon)
            self.SetItemText(layer, '%s %s' % (_('vector'), label))
        elif ltype == 'thememap':
            self.SetItemImage(layer, self.theme_icon)
            self.SetItemText(layer, '%s %s' % (_('thematic area (choropleth) map'), label))
        elif ltype == 'themechart':
            self.SetItemImage(layer, self.chart_icon)
            self.SetItemText(layer, '%s %s' % (_('thematic charts'), label))
        elif ltype == 'grid':
            self.SetItemImage(layer, self.grid_icon)
            self.SetItemText(layer, '%s %s' % (_('grid'), label))
        elif ltype == 'geodesic':
            self.SetItemImage(layer, self.geodesic_icon)
            self.SetItemText(layer, '%s %s' % (_('geodesic line'), label))
        elif ltype == 'rhumb':
            self.SetItemImage(layer, self.rhumb_icon)
            self.SetItemText(layer, '%s %s' % (_('rhumbline'), label))
        elif ltype == 'labels':
            self.SetItemImage(layer, self.labels_icon)
            self.SetItemText(layer, '%s %s' % (_('vector labels'), label))
        elif ltype == 'command':
            self.SetItemImage(layer, self.cmd_icon)
        elif ltype == 'group':
            self.SetItemImage(layer, self.folder)
            self.SetItemText(layer, grouptext)
        
        self.first = False
        
        if ltype != 'group':
            if lcmd and len(lcmd) > 1:
                cmd = lcmd
                render = False
                name, found = utils.GetLayerNameFromCmd(lcmd)
            else:
                cmd = []
                if ltype == 'command' and lname:
                    for c in lname.split(';'):
                        cmd.append(c.split(' '))
                
                render = False
                name = None
            
            if ctrl:
                ctrlId = ctrl.GetId()
            else:
                ctrlId = None
                
            # add a data object to hold the layer's command (does not apply to generic command layers)
            self.SetPyData(layer, ({'cmd'      : cmd,
                                    'type'     : ltype,
                                    'ctrl'     : ctrlId,
                                    'label'    : None,
                                    'maplayer' : None,
                                    'vdigit'   : lvdigit,
                                    'nviz'     : lnviz,
                                    'propwin'  : None}, 
                                   None))
            
            # find previous map layer instance 
            prevItem = self.GetFirstChild(self.root)[0]
            prevMapLayer = None 
            pos = -1
            while prevItem and prevItem.IsOk() and prevItem != layer: 
                if self.GetPyData(prevItem)[0]['maplayer']: 
                    prevMapLayer = self.GetPyData(prevItem)[0]['maplayer'] 
                
                prevItem = self.GetNextSibling(prevItem) 
                
                if prevMapLayer: 
                    pos = self.Map.GetLayerIndex(prevMapLayer)
                else: 
                    pos = -1
            
            maplayer = self.Map.AddLayer(pos = pos,
                                         type = ltype, command = self.GetPyData(layer)[0]['cmd'], name = name,
                                         l_active = checked, l_hidden = False,
                                         l_opacity = lopacity, l_render = render)
            self.GetPyData(layer)[0]['maplayer'] = maplayer
            
            # run properties dialog if no properties given
            if len(cmd) == 0:
                self.PropertiesDialog(layer, show = True)
        
        else: # group
            self.SetPyData(layer, ({'cmd'      : None,
                                    'type'     : ltype,
                                    'ctrl'     : None,
                                    'label'    : None,
                                    'maplayer' : None,
                                    'propwin'  : None}, 
                                   None))
        
        # select new item
        self.SelectItem(layer, select = True)
        self.layer_selected = layer
        
        # use predefined layer name if given
        if lname:
            if ltype == 'group':
                self.SetItemText(layer, lname)
            elif ltype == 'command':
                ctrl.SetValue(lname)
            else:
                self.SetItemText(layer, self._getLayerName(layer, lname))
        else:
            if ltype == 'group':
                self.OnRenameLayer(None)
        
        # updated progress bar range (mapwindow statusbar)
        if checked:
            self.mapdisplay.statusbarWin['progress'].SetRange(len(self.Map.GetListOfLayers(l_active = True)))
            
        return layer

    def PropertiesDialog (self, layer, show = True):
        """!Launch the properties dialog"""
        if 'propwin' in self.GetPyData(layer)[0] and \
                self.GetPyData(layer)[0]['propwin'] is not None:
            # recycle GUI dialogs
            win = self.GetPyData(layer)[0]['propwin']
            # update properties (columns, layers)
            win.notebookpanel.OnUpdateSelection(None)
            if win.IsShown():
                win.SetFocus()
            else:
                win.Show()
            
            return
        
        completed = ''
        params = self.GetPyData(layer)[1]
        ltype  = self.GetPyData(layer)[0]['type']
                
        Debug.msg (3, "LayerTree.PropertiesDialog(): ltype=%s" % \
                   ltype)
        
        if self.GetPyData(layer)[0]['cmd']:
            module = menuform.GUI(parent = self, show = show, centreOnParent = False)
            module.ParseCommand(self.GetPyData(layer)[0]['cmd'],
                                completed = (self.GetOptData,layer,params))
            
            self.GetPyData(layer)[0]['cmd'] = module.GetCmd()
        elif ltype == 'raster':
            cmd = ['d.rast']
            
            if UserSettings.Get(group = 'cmd', key = 'rasterOpaque', subkey = 'enabled'):
                cmd.append('-n')
            menuform.GUI(parent = self, centreOnParent = False).ParseCommand(cmd,
                                                                             completed = (self.GetOptData,layer,params))
                         
        elif ltype == '3d-raster':
            cmd = ['d.rast3d']
            menuform.GUI(parent = self, centreOnParent = False).ParseCommand(cmd,
                                                                             completed = (self.GetOptData,layer,params))
                                        
        elif ltype == 'rgb':
            menuform.GUI(parent = self, centreOnParent = False).ParseCommand(['d.rgb'],
                                                                             completed = (self.GetOptData,layer,params))
            
        elif ltype == 'his':
            menuform.GUI(parent = self, centreOnParent = False).ParseCommand(['d.his'],
                                                                             completed = (self.GetOptData,layer,params))
            
        elif ltype == 'shaded':
            menuform.GUI(parent = self, centreOnParent = False).ParseCommand(['d.shadedmap'],
                                                                             completed = (self.GetOptData,layer,params))
            
        elif ltype == 'rastarrow':
            menuform.GUI(parent = self, centreOnParent = False).ParseCommand(['d.rast.arrow'],
                                                                             completed = (self.GetOptData,layer,params))
            
        elif ltype == 'rastnum':
            menuform.GUI(parent = self, centreOnParent = False).ParseCommand(['d.rast.num'],
                                                                             completed = (self.GetOptData,layer,params))
            
        elif ltype == 'vector':
            types = list()
            for ftype in ['point', 'line', 'boundary', 'centroid', 'area', 'face']:
                if UserSettings.Get(group = 'cmd', key = 'showType', subkey = [ftype, 'enabled']):
                    types.append(ftype)
            
            menuform.GUI(parent = self, centreOnParent = False).ParseCommand(['d.vect', 'type=%s' % ','.join(types)],
                                                                             completed = (self.GetOptData,layer,params))
            
        elif ltype == 'thememap':
            # -s flag requested, otherwise only first thematic category is displayed
            # should be fixed by C-based d.thematic.* modules
            menuform.GUI(parent = self, centreOnParent = False).ParseCommand(['d.thematic.area'], 
                                                                             completed = (self.GetOptData,layer,params))
            
        elif ltype == 'themechart':
            menuform.GUI(parent = self, centreOnParent = False).ParseCommand(['d.vect.chart'],
                                                                             completed = (self.GetOptData,layer,params))
            
        elif ltype == 'grid':
            menuform.GUI(parent = self, centreOnParent = False).ParseCommand(['d.grid'],
                                                                             completed = (self.GetOptData,layer,params))
            
        elif ltype == 'geodesic':
            menuform.GUI(parent = self, centreOnParent = False).ParseCommand(['d.geodesic'],
                                                                             completed = (self.GetOptData,layer,params))
            
        elif ltype == 'rhumb':
            menuform.GUI(parent = self, centreOnParent = False).ParseCommand(['d.rhumbline'],
                                                                             completed = (self.GetOptData,layer,params))
            
        elif ltype == 'labels':
            menuform.GUI(parent = self, centreOnParent = False).ParseCommand(['d.labels'],
                                                                             completed = (self.GetOptData,layer,params))
            
        elif ltype == 'cmdlayer':
            pass
        elif ltype == 'group':
            pass
        
    def OnActivateLayer(self, event):
        """!Double click on the layer item.
        Launch property dialog, or expand/collapse group of items, etc.
        """
        self.lmgr.WorkspaceChanged()
        layer = event.GetItem()
        self.layer_selected = layer
        
        self.PropertiesDialog(layer)
        
        if self.GetPyData(layer)[0]['type'] == 'group':
            if self.IsExpanded(layer):
                self.Collapse(layer)
            else:
                self.Expand(layer)
        
    def OnDeleteLayer(self, event):
        """!Remove selected layer item from the layer tree"""
        self.lmgr.WorkspaceChanged()
        item = event.GetItem()
        
        try:
            item.properties.Close(True)
        except:
            pass

        if item != self.root:
            Debug.msg (3, "LayerTree.OnDeleteLayer(): name=%s" % \
                           (self.GetItemText(item)))
        else:
            self.root = None

        # unselect item
        self.Unselect()
        self.layer_selected = None

        try:
            if self.GetPyData(item)[0]['type'] != 'group':
                self.Map.DeleteLayer( self.GetPyData(item)[0]['maplayer'])
        except:
            pass

        # redraw map if auto-rendering is enabled
        self.rerender = True
        self.reorder = True
        
        if self.mapdisplay.toolbars['vdigit']:
            self.mapdisplay.toolbars['vdigit'].UpdateListOfLayers (updateTool = True)

        # update progress bar range (mapwindow statusbar)
        self.mapdisplay.statusbarWin['progress'].SetRange(len(self.Map.GetListOfLayers(l_active = True)))

        #
        # nviz
        #
        if self.lmgr.IsPaneShown('toolbarNviz') and \
                self.GetPyData(item) is not None and not self.rerender:
            # nviz - load/unload data layer
            mapLayer = self.GetPyData(item)[0]['maplayer']
            self.mapdisplay.SetStatusText(_("Please wait, updating data..."), 0)
            if mapLayer.type == 'raster':
                self.mapdisplay.MapWindow.UnloadRaster(item)
            elif mapLayer.type == '3d-raster':
                self.mapdisplay.MapWindow.UnloadRaster3d(item)
            elif mapLayer.type == 'vector':
                self.mapdisplay.MapWindow.UnloadVector(item)
            self.mapdisplay.SetStatusText("", 0)
            
        event.Skip()

    def OnLayerChecked(self, event):
        """!Enable/disable data layer"""
        self.lmgr.WorkspaceChanged()
        
        item    = event.GetItem()
        checked = item.IsChecked()

        digitToolbar = self.mapdisplay.toolbars['vdigit']
        if self.first == False:
            # change active parameter for item in layers list in render.Map
            if self.GetPyData(item)[0]['type'] == 'group':
                child, cookie = self.GetFirstChild(item)
                while child:
                    self.CheckItem(child, checked)
                    mapLayer = self.GetPyData(child)[0]['maplayer']
                    if not digitToolbar or \
                           (digitToolbar and digitToolbar.GetLayer() != mapLayer):
                        # ignore when map layer is edited
                        self.Map.ChangeLayerActive(mapLayer, checked)
                    child = self.GetNextSibling(child)
            else:
                mapLayer = self.GetPyData(item)[0]['maplayer']
                if not digitToolbar or \
                       (digitToolbar and digitToolbar.GetLayer() != mapLayer):
                    # ignore when map layer is edited
                    self.Map.ChangeLayerActive(mapLayer, checked)

        #
        # update progress bar range (mapwindow statusbar)
        #
        self.mapdisplay.statusbarWin['progress'].SetRange(len(self.Map.GetListOfLayers(l_active = True)))

        #
        # nviz
        #
        if self.lmgr.IsPaneShown('toolbarNviz') and \
                self.GetPyData(item) is not None:
            # nviz - load/unload data layer
            mapLayer = self.GetPyData(item)[0]['maplayer']

            self.mapdisplay.SetStatusText(_("Please wait, updating data..."), 0)

            if checked: # enable
                if mapLayer.type == 'raster':
                    self.mapdisplay.MapWindow.LoadRaster(item)
                elif mapLayer.type == '3d-raster':
                    self.mapdisplay.MapWindow.LoadRaster3d(item)
                elif mapLayer.type == 'vector':
                    npoints, nlines, nfeatures, mapIs3D = self.lmgr.nviz.VectorInfo(mapLayer)
                    if npoints > 0:
                        self.mapdisplay.MapWindow.LoadVector(item, points = True)
                    if nlines > 0:
                        self.mapdisplay.MapWindow.LoadVector(item, points = False)

            else: # disable
                if mapLayer.type == 'raster':
                    self.mapdisplay.MapWindow.UnloadRaster(item)
                elif mapLayer.type == '3d-raster':
                    self.mapdisplay.MapWindow.UnloadRaster3d(item)
                elif mapLayer.type == 'vector':
                    self.mapdisplay.MapWindow.UnloadVector(item)
            
            self.mapdisplay.SetStatusText("", 0)

        # redraw map if auto-rendering is enabled
        self.rerender = True
        self.reorder = True
        
    def OnCmdChanged(self, event):
        """!Change command string"""
        ctrl = event.GetEventObject().GetId()
        cmd = event.GetString()
        
        layer = self.GetFirstVisibleItem()

        while layer and layer.IsOk():
            if self.GetPyData(layer)[0]['ctrl'] == ctrl:
                break
            
            layer = self.GetNextVisible(layer)

        # change parameters for item in layers list in render.Map
        self.ChangeLayer(layer)
        
        event.Skip()

    def OnChangeSel(self, event):
        """!Selection changed"""
        oldlayer = event.GetOldItem()
        layer = event.GetItem()
        if layer == oldlayer:
            event.Veto()
            return
        
        digitToolbar = self.mapdisplay.toolbars['vdigit']
        if digitToolbar:
            mapLayer = self.GetPyData(layer)[0]['maplayer']
            bgmap = UserSettings.Get(group = 'vdigit', key = 'bgmap', subkey = 'value',
                                     internal = True)
            
            if digitToolbar.GetLayer() == mapLayer:
                self._setGradient('vdigit')
            elif bgmap == mapLayer.GetName():
                self._setGradient('bgmap')
            else:
                self._setGradient()
        else:
            self._setGradient()
        
        self.layer_selected = layer
        
        try:
            if self.IsSelected(oldlayer):
                self.SetItemWindowEnabled(oldlayer, True)
            else:
                self.SetItemWindowEnabled(oldlayer, False)

            if self.IsSelected(layer):
                self.SetItemWindowEnabled(layer, True)
            else:
                self.SetItemWindowEnabled(layer, False)
        except:
            pass
        
        try:
            self.RefreshLine(oldlayer)
            self.RefreshLine(layer)
        except:
            pass
        
        # update statusbar -> show command string
        if self.GetPyData(layer) and self.GetPyData(layer)[0]['maplayer']:
            cmd = self.GetPyData(layer)[0]['maplayer'].GetCmd(string = True)
            if len(cmd) > 0:
                self.lmgr.SetStatusText(cmd)
        
        # set region if auto-zooming is enabled
        if self.GetPyData(layer) and self.GetPyData(layer)[0]['cmd'] and \
               UserSettings.Get(group = 'display', key = 'autoZooming', subkey = 'enabled'):
            mapLayer = self.GetPyData(layer)[0]['maplayer']
            if mapLayer.GetType() in ('raster', 'vector'):
                render = self.mapdisplay.statusbarWin['render'].IsChecked()
                self.mapdisplay.MapWindow.ZoomToMap(layers = [mapLayer,],
                                                    render = render)
        
        # update nviz tools
        if self.lmgr.IsPaneShown('toolbarNviz') and \
                self.GetPyData(self.layer_selected) is not None:
            if self.layer_selected.IsChecked():
                # update Nviz tool window
                type = self.GetPyData(self.layer_selected)[0]['maplayer'].type
                
                if type == 'raster':
                    self.lmgr.nviz.UpdatePage('surface')
                    self.lmgr.nviz.SetPage('surface')
                elif type == 'vector':
                    self.lmgr.nviz.UpdatePage('vector')
                    self.lmgr.nviz.SetPage('vector')
                elif type == '3d-raster':
                    self.lmgr.nviz.UpdatePage('volume')
                    self.lmgr.nviz.SetPage('volume')
        
    def OnCollapseNode(self, event):
        """!Collapse node
        """
        if self.GetPyData(self.layer_selected)[0]['type'] == 'group':
            self.SetItemImage(self.layer_selected, self.folder)

    def OnExpandNode(self, event):
        """!Expand node
        """
        self.layer_selected = event.GetItem()
        if self.GetPyData(self.layer_selected)[0]['type'] == 'group':
            self.SetItemImage(self.layer_selected, self.folder_open)
    
    def OnEndDrag(self, event):
        self.StopDragging()
        dropTarget = event.GetItem()
        self.flag = self.HitTest(event.GetPoint())[1]
        if self.lmgr.IsPaneShown('toolbarNviz'):
            self.mapdisplay.MapWindow.UnloadDataLayers(True)
        if self.IsValidDropTarget(dropTarget):
            self.UnselectAll()
            if dropTarget != None:
                self.SelectItem(dropTarget)
            self.OnDrop(dropTarget, self._dragItem)
        elif dropTarget == None:
            self.OnDrop(dropTarget, self._dragItem)
            
    def OnDrop(self, dropTarget, dragItem):
        # save everthing associated with item to drag
        try:
            old = dragItem  # make sure this member exists
        except:
            return

        Debug.msg (4, "LayerTree.OnDrop(): layer=%s" % \
                   (self.GetItemText(dragItem)))
        
        # recreate data layer, insert copy of layer in new position, and delete original at old position
        newItem  = self.RecreateItem (dragItem, dropTarget)

        # if recreated layer is a group, also recreate its children
        if  self.GetPyData(newItem)[0]['type'] == 'group':
            (child, cookie) = self.GetFirstChild(dragItem)
            if child:
                while child:
                    self.RecreateItem(child, dropTarget, parent = newItem)
                    self.Delete(child)
                    child = self.GetNextChild(old, cookie)[0]
        
        # delete layer at original position
        try:
            self.Delete(old) # entry in render.Map layers list automatically deleted by OnDeleteLayer handler
        except AttributeError:
            pass

        # redraw map if auto-rendering is enabled
        self.rerender = True
        self.reorder = True
        
        # select new item
        self.SelectItem(newItem)
        
    def RecreateItem (self, dragItem, dropTarget, parent = None):
        """!Recreate item (needed for OnEndDrag())
        """
        Debug.msg (4, "LayerTree.RecreateItem(): layer=%s" % \
                   self.GetItemText(dragItem))

        # fetch data (dragItem)
        checked = self.IsItemChecked(dragItem)
        image   = self.GetItemImage(dragItem, 0)
        text    = self.GetItemText(dragItem)
        if self.GetPyData(dragItem)[0]['ctrl']:
            # recreate data layer
            btnbmp = Icons['layerManager']["layerOptions"].GetBitmap((16,16))
            newctrl = buttons.GenBitmapButton(self, id = wx.ID_ANY, bitmap = btnbmp, size = (24, 24))
            newctrl.SetToolTipString(_("Click to edit layer settings"))
            self.Bind(wx.EVT_BUTTON, self.OnLayerContextMenu, newctrl)
            data    = self.GetPyData(dragItem)
        
        elif self.GetPyData(dragItem)[0]['type'] == 'command':
            # recreate command layer
            oldctrl = None
            newctrl = wx.TextCtrl(self, id = wx.ID_ANY, value = '',
                                  pos = wx.DefaultPosition, size = (250,25),
                                  style = wx.TE_MULTILINE|wx.TE_WORDWRAP)
            try:
                newctrl.SetValue(self.GetPyData(dragItem)[0]['maplayer'].GetCmd(string = True))
            except:
                pass
            newctrl.Bind(wx.EVT_TEXT_ENTER, self.OnCmdChanged)
            newctrl.Bind(wx.EVT_TEXT,       self.OnCmdChanged)
            data    = self.GetPyData(dragItem)

        elif self.GetPyData(dragItem)[0]['type'] == 'group':
            # recreate group
            newctrl = None
            data    = None
            
        # decide where to put recreated item
        if dropTarget != None and dropTarget != self.GetRootItem():
            if parent:
                # new item is a group
                afteritem = parent
            else:
                # new item is a single layer
                afteritem = dropTarget

            # dragItem dropped on group
            if  self.GetPyData(afteritem)[0]['type'] == 'group':
                newItem = self.PrependItem(afteritem, text = text, \
                                      ct_type = 1, wnd = newctrl, image = image, \
                                      data = data)
                self.Expand(afteritem)
            else:
                #dragItem dropped on single layer
                newparent = self.GetItemParent(afteritem)
                newItem = self.InsertItem(newparent, self.GetPrevSibling(afteritem), \
                                       text = text, ct_type = 1, wnd = newctrl, \
                                       image = image, data = data)
        else:
            # if dragItem not dropped on a layer or group, append or prepend it to the layer tree
            if self.flag & wx.TREE_HITTEST_ABOVE:
                newItem = self.PrependItem(self.root, text = text, \
                                      ct_type = 1, wnd = newctrl, image = image, \
                                      data = data)
            elif (self.flag &  wx.TREE_HITTEST_BELOW) or (self.flag & wx.TREE_HITTEST_NOWHERE) \
                     or (self.flag & wx.TREE_HITTEST_TOLEFT) or (self.flag & wx.TREE_HITTEST_TORIGHT):
                newItem = self.AppendItem(self.root, text = text, \
                                      ct_type = 1, wnd = newctrl, image = image, \
                                      data = data)

        #update new layer 
        self.SetPyData(newItem, self.GetPyData(dragItem))
        if newctrl:
            self.GetPyData(newItem)[0]['ctrl'] = newctrl.GetId()
        else:
            self.GetPyData(newItem)[0]['ctrl'] = None
            
        self.CheckItem(newItem, checked = checked) # causes a new render
        
        return newItem

    def _getLayerName(self, item, lname = ''):
        """!Get layer name string

        @param lname optional layer name
        """
        mapLayer = self.GetPyData(item)[0]['maplayer']
        if not mapLayer:
            return lname
        
        if not lname:
            lname  = self.GetPyData(item)[0]['label']
        opacity  = int(mapLayer.GetOpacity(float = True) * 100)
        if not lname:
            dcmd    = self.GetPyData(item)[0]['cmd']
            lname, found = utils.GetLayerNameFromCmd(dcmd, layerType = mapLayer.GetType(),
                                                     fullyQualified = True)
            if not found:
                return None
        
        if opacity < 100:
            return lname + ' (%s %d' % (_('opacity:'), opacity) + '%)'
        
        return lname
                
    def GetOptData(self, dcmd, layer, params, propwin):
        """!Process layer data (when changes in properties dialog are applied)
        """
        # set layer text to map name
        if dcmd:
            self.GetPyData(layer)[0]['cmd'] = dcmd
            mapText  = self._getLayerName(layer)
            mapName, found = utils.GetLayerNameFromCmd(dcmd)
            mapLayer = self.GetPyData(layer)[0]['maplayer']
            self.SetItemText(layer, mapName)
            
            if not mapText or not found:
                propwin.Hide()
                GWarning(parent = self,
                         message = _("Map <%s> not found.") % mapName)
                return
        
        # update layer data
        if params:
            self.SetPyData(layer, (self.GetPyData(layer)[0], params))
        self.GetPyData(layer)[0]['propwin'] = propwin
        
        # change parameters for item in layers list in render.Map
        self.ChangeLayer(layer)
        
        # set region if auto-zooming is enabled
        if dcmd and UserSettings.Get(group = 'display', key = 'autoZooming', subkey = 'enabled'):
            mapLayer = self.GetPyData(layer)[0]['maplayer']
            if mapLayer.GetType() in ('raster', 'vector'):
                render = UserSettings.Get(group = 'display', key = 'autoRendering', subkey = 'enabled')
                self.mapdisplay.MapWindow.ZoomToMap(layers = [mapLayer,],
                                                    render = render)
        
        # update nviz session        
        if self.lmgr.IsPaneShown('toolbarNviz') and dcmd:
            mapLayer = self.GetPyData(layer)[0]['maplayer']
            mapWin = self.mapdisplay.MapWindow
            if len(mapLayer.GetCmd()) > 0:
                id = -1
                if mapLayer.type == 'raster':
                    if mapWin.IsLoaded(layer):
                        mapWin.UnloadRaster(layer)
                    
                    mapWin.LoadRaster(layer)
                    
                elif mapLayer.type == '3d-raster':
                    if mapWin.IsLoaded(layer):
                        mapWin.UnloadRaster3d(layer)
                    
                    mapWin.LoadRaster3d(layer)
                    
                elif mapLayer.type == 'vector':
                    if mapWin.IsLoaded(layer):
                        mapWin.UnloadVector(layer)
                    
                    mapWin.LoadVector(layer)

                # reset view when first layer loaded
                nlayers = len(mapWin.Map.GetListOfLayers(l_type = ('raster', 'vector'),
                                                         l_active = True))
                if nlayers < 2:
                    mapWin.ResetView()
        
    def ReorderLayers(self):
        """!Add commands from data associated with any valid layers
        (checked or not) to layer list in order to match layers in
        layer tree."""

        # make a list of visible layers
        treelayers = []
        
        vislayer = self.GetFirstVisibleItem()
        
        if not vislayer or self.GetPyData(vislayer) is None:
            return
        
        itemList = ""
        
        for item in range(self.GetCount()):
            itemList += self.GetItemText(vislayer) + ','
            if self.GetPyData(vislayer)[0]['type'] != 'group':
                treelayers.append(self.GetPyData(vislayer)[0]['maplayer'])

            if not self.GetNextVisible(vislayer):
                break
            else:
                vislayer = self.GetNextVisible(vislayer)
        
        Debug.msg (4, "LayerTree.ReorderLayers(): items=%s" % \
                   (itemList))
        
        # reorder map layers
        treelayers.reverse()
        self.Map.ReorderLayers(treelayers)
        self.reorder = False
        
    def ChangeLayer(self, item):
        """!Change layer"""
        type = self.GetPyData(item)[0]['type']
        layerName = None
        
        if type == 'command':
            win = self.FindWindowById(self.GetPyData(item)[0]['ctrl'])
            if win.GetValue() != None:
                cmd = win.GetValue().split(';')
                cmdlist = []
                for c in cmd:
                    cmdlist.append(c.split(' '))
                opac = 1.0
                chk = self.IsItemChecked(item)
                hidden = not self.IsVisible(item)
        elif type != 'group':
            if self.GetPyData(item)[0] is not None:
                cmdlist = self.GetPyData(item)[0]['cmd']
                opac = self.GetPyData(item)[0]['maplayer'].GetOpacity(float = True)
                chk = self.IsItemChecked(item)
                hidden = not self.IsVisible(item)
                # determine layer name
                layerName, found = utils.GetLayerNameFromCmd(cmdlist, fullyQualified = True)
                if not found:
                    layerName = self.GetItemText(item)
        
        maplayer = self.Map.ChangeLayer(layer = self.GetPyData(item)[0]['maplayer'], type = type,
                                        command = cmdlist, name = layerName,
                                        l_active = chk, l_hidden = hidden, l_opacity = opac, l_render = False)
        
        self.GetPyData(item)[0]['maplayer'] = maplayer
        
        # if digitization tool enabled -> update list of available vector map layers
        if self.mapdisplay.toolbars['vdigit']:
            self.mapdisplay.toolbars['vdigit'].UpdateListOfLayers(updateTool = True)
        
        # redraw map if auto-rendering is enabled
        self.rerender = True
        self.reorder = True
        
    def OnCloseWindow(self, event):
        pass
        # self.Map.Clean()

    def FindItemByData(self, key, value):
        """!Find item based on key and value (see PyData[0]).
        
        If key is 'name', finds item(s) of given maplayer name.
        
        @return item instance
        @return None not found
        """
        item = self.GetFirstChild(self.root)[0]
        if key == 'name':
            return self.__FindSubItemByName(item, value)
        else:
            return self.__FindSubItemByData(item, key, value)

    def FindItemByIndex(self, index):
        """!Find item by index (starting at 0)

        @return item instance
        @return None not found
        """
        item = self.GetFirstChild(self.root)[0]
        i = 0
        while item and item.IsOk():
            if i == index:
                return item
            
            item = self.GetNextVisible(item)
            i += 1
        
        return None
    
    def EnableItemType(self, type, enable = True):
        """!Enable/disable items in layer tree"""
        item = self.GetFirstChild(self.root)[0]
        while item and item.IsOk():
            mapLayer = self.GetPyData(item)[0]['maplayer']
            if mapLayer and type == mapLayer.type:
                self.EnableItem(item, enable)
            
            item = self.GetNextSibling(item)
        
    def __FindSubItemByData(self, item, key, value):
        """!Support method for FindItemByData"""
        while item and item.IsOk():
            try:
                itemValue = self.GetPyData(item)[0][key]
            except KeyError:
                return None
            
            if value == itemValue:
                return item
            if self.GetPyData(item)[0]['type'] == 'group':
                subItem = self.GetFirstChild(item)[0]
                found = self.__FindSubItemByData(subItem, key, value)
                if found:
                    return found
            item = self.GetNextSibling(item)

        return None

    def __FindSubItemByName(self, item, value):
        """!Support method for FindItemByData for searching by name"""
        items = []
        while item and item.IsOk():
            try:
                itemLayer = self.GetPyData(item)[0]['maplayer']
            except KeyError:
                return None
            
            if value == itemLayer.GetName():
                items.append(item)
            if self.GetPyData(item)[0]['type'] == 'group':
                subItem = self.GetFirstChild(item)[0]
                found = self.__FindSubItemByName(subItem, name)
                if found:
                    items.extend(found)
            item = self.GetNextSibling(item)
        
        if items:
            return items
        return None
