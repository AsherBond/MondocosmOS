/***********************************************************************************************
 * File Info: $Id: SwingTest.java,v 1.5 2003/03/04 01:10:09 nathaniel_auvil Exp $
 * Copyright (C) 2002
 * Author: Nathaniel G. Auvil
 * Contributor(s):
 *
 * Copyright 2002 (C) Nathaniel G. Auvil. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation ("Software"), with or
 * without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and notices.
 * 	Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * 	conditions and the following disclaimer in the documentation and/or other materials
 * 	provided with the distribution.
 *
 * 3. The name "jCharts" or "Nathaniel G. Auvil" must not be used to endorse or promote
 * 	products derived from this Software without prior written permission of Nathaniel G.
 * 	Auvil.  For written permission, please contact nathaniel_auvil@users.sourceforge.net
 *
 * 4. Products derived from this Software may not be called "jCharts" nor may "jCharts" appear
 * 	in their names without prior written permission of Nathaniel G. Auvil. jCharts is a
 * 	registered trademark of Nathaniel G. Auvil.
 *
 * 5. Due credit should be given to the jCharts Project (http://jcharts.sourceforge.net/).
 *
 * THIS SOFTWARE IS PROVIDED BY Nathaniel G. Auvil AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * jCharts OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 ************************************************************************************************/


package org.jCharts.test;


import org.jCharts.chartData.ChartDataException;
import org.jCharts.chartData.PieChartDataSet;
import org.jCharts.nonAxisChart.PieChart2D;
import org.jCharts.properties.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;


public class SwingTest extends JFrame
{
	private JPanel panel;


	/*******************************************************************************
	 *
	 ********************************************************************************/
	public SwingTest() throws ChartDataException, PropertyException
	{
		initComponents();
	}


	/*******************************************************************************
	 *
	 ********************************************************************************/
	private void initComponents() throws ChartDataException, PropertyException
	{
		this.setSize( 500, 500 );
		this.panel=new JPanel( true );
		this.panel.setSize( 500, 500 );
		this.getContentPane().add( this.panel );
		this.setVisible( true );


		String[] labels={"BMW", "Audi", "Lexus"};
		String title="Cars that Own";
		Paint[] paints={Color.blue, Color.gray, Color.red};
		double[] data={50d, 30d, 20d};

		PieChart2DProperties pieChart2DProperties=new PieChart2DProperties();
		PieChartDataSet pieChartDataSet=new PieChartDataSet( title, data, labels, paints, pieChart2DProperties );

		PieChart2D pieChart2D=new PieChart2D( pieChartDataSet, new LegendProperties(), new ChartProperties(), 450, 450 );

		//BufferedImage bufferedImage=new BufferedImage( 450, 450, BufferedImage.TYPE_INT_RGB );
		//pieChart2D.setGraphics2D( bufferedImage.createGraphics() );

		pieChart2D.setGraphics2D( (Graphics2D) this.panel.getGraphics() );
		pieChart2D.render();


		//this.panel.getGraphics().drawImage( bufferedImage, 0, 0, this );


		addWindowListener( new java.awt.event.WindowAdapter()
		{
			public void windowClosing( WindowEvent windowEvent )
			{
				exitForm( windowEvent );
			}
		}
		);
	}


	/*********************************************************************************
	 * Exit the Application
	 *
	 * @param windowEvent
	 ***********************************************************************************/
	private void exitForm( WindowEvent windowEvent )
	{
		System.exit( 0 );
	}


	/*********************************************************************************
	 *
	 *
	 ***********************************************************************************/
	public static void main( String args[] ) throws ChartDataException, PropertyException
	{
		new SwingTest();
	}


}
