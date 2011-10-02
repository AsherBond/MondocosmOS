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


#include "cubetoolwidget.h"

#include "3dmprefs.h"

#include <QtGui/QDockWidget>
#include <QtGui/QLayout>
#include <QtGui/QLabel>
#include <QtGui/QHBoxLayout>
#include <QtGui/QVBoxLayout>
#include <QtGui/QGroupBox>
#include <QtGui/QSpinBox>
#include <QtGui/QCheckBox>

CubeToolWidget::CubeToolWidget( Observer * observer, QMainWindow * parent )
   : ToolWidget( parent ),
     m_observer( observer )
{
   const int  DEFAULT_SEGMENT = 1;
   const bool DEFAULT_CUBE    = false;

   m_layout = boxLayout();

   m_cubeLabel = new QLabel( tr("Cube"), mainWidget() );
   m_layout->addWidget( m_cubeLabel );

   m_cubeValue = new QCheckBox( mainWidget() );
   m_layout->addWidget( m_cubeValue );

   bool isCube = DEFAULT_CUBE;
   if ( g_prefs.exists( "ui_cubetool_iscube" ) )
   {
      isCube = (g_prefs( "ui_cubetool_iscube" ).intValue() != 0) ? true : false;
   }
   m_cubeValue->setChecked( isCube );

   m_segmentLabel = new QLabel( tr("Segment"), mainWidget() );
   m_layout->addWidget( m_segmentLabel );

   m_segmentValue = new QSpinBox( mainWidget() );
   m_layout->addWidget( m_segmentValue );

   m_segmentValue->setMinimum( 1 );
   m_segmentValue->setMaximum( 25 );
   int segmentVal = DEFAULT_SEGMENT;
   if ( g_prefs.exists( "ui_cubetool_segment" ) )
   {
      int val = g_prefs( "ui_cubetool_segment" ).intValue();
      if ( val >= 1 && val <= 25 )
      {
         segmentVal = val;
      }
   }
   m_segmentValue->setValue( segmentVal );

   m_layout->addStretch();

   connect( m_cubeValue,  SIGNAL(toggled(bool)), this, SLOT(cubeValueChanged(bool))  );
   connect( m_segmentValue, SIGNAL(valueChanged(int)), this, SLOT(segmentValueChanged(int)) );

   m_cubeLabel->show();
   m_cubeValue->show();
   m_segmentLabel->show();
   m_segmentValue->show();

   cubeValueChanged( isCube );
   segmentValueChanged( segmentVal );
   m_layout->addStretch();
}

CubeToolWidget::~CubeToolWidget()
{
}

void CubeToolWidget::segmentValueChanged( int newValue )
{
   g_prefs( "ui_cubetool_segment" ) = newValue;
   m_observer->setSegmentValue( newValue );
}

void CubeToolWidget::cubeValueChanged( bool newValue )
{
   g_prefs( "ui_cubetool_iscube" ) = newValue ? 1 : 0;
   m_observer->setCubeValue( newValue );
}

