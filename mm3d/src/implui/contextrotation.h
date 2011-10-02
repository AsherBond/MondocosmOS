/*  Misfit Model 3D
 * 
 *  Copyright (c) 2004-2007 Kevin Worcester
 * 
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, 
 *  USA.
 *
 *  See the COPYING file for full license text.
 */


#ifndef __CONTEXTROTATION_H
#define __CONTEXTROTATION_H

#include "contextrotation.base.h"

#include <QtGui/QWidget>

#include "contextwidget.h"

class Model;

class ContextRotation : public QWidget, public Ui::ContextRotationBase, public ContextWidget
{
   Q_OBJECT
   public:
      ContextRotation( QWidget * parent );
      virtual ~ContextRotation();

      // ContextWidget methods
      void setModel( Model * );
      void modelChanged( int changeBits );
      bool isUpdating() { return m_update; };

   signals:
      void panelChange();

   public slots:
      // Rotation slots
      void updateRotation();

   protected:
      Model * m_model;
      bool    m_change;
      bool    m_update;
};

#endif // __CONTEXTROTATION_H
