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


#include "polytoolwidget.h"

#include "3dmprefs.h"

#include <QtGui/QLabel>
#include <QtGui/QLayout>
#include <QtGui/QHBoxLayout>
#include <QtGui/QVBoxLayout>
#include <QtGui/QGroupBox>
#include <QtGui/QSpinBox>
#include <QtGui/QComboBox>

PolyToolWidget::PolyToolWidget( Observer * observer, QMainWindow * parent )
   : ToolWidget ( parent ),
     m_observer( observer )
{
   const int DEFAULT_FAN  = 0;

   m_layout = boxLayout();

   m_typeLabel = new QLabel( tr("Poly Type"), mainWidget() );
   m_layout->addWidget( m_typeLabel );

   m_typeValue = new QComboBox( mainWidget() );
   m_typeValue->insertItem( 0, tr("Strip", "Triangle strip option") );
   m_typeValue->insertItem( 1, tr("Fan", "Triangle fan option") );
   m_layout->addWidget( m_typeValue );

   g_prefs.setDefault( "ui_polytool_is_fan", DEFAULT_FAN );
   int index = g_prefs( "ui_polytool_isfan" ).intValue();
   m_typeValue->setCurrentIndex( (index == 0) ? 0 : 1 );

   m_layout->addStretch();

   connect( m_typeValue,  SIGNAL(activated(int)), this, SLOT(typeValueChanged(int))  );

   m_typeLabel->show();
   m_typeValue->show();

   typeValueChanged( index );
}

PolyToolWidget::~PolyToolWidget()
{
}

void PolyToolWidget::typeValueChanged( int newValue )
{
   g_prefs( "ui_polytool_isfan" ) = newValue;
   m_observer->setTypeValue( newValue );
}

