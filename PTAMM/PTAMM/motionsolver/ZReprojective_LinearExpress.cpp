// ZReprojective_LinearExpress.cpp: implementation of the ZReprojective_LinearExpress class.
//
//////////////////////////////////////////////////////////////////////

#include "ZReprojective_LinearExpress.h"
#include "globalMath.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

ZReprojective_LinearExpress::ZReprojective_LinearExpress()
{
	w = 1.0;
	m_varIndex.resize(3);
	m_varIndex[0] = 0;
	m_varIndex[1] = 1;
	m_varIndex[2] = 2;
}

ZReprojective_LinearExpress::~ZReprojective_LinearExpress()
{

}


void ZReprojective_LinearExpress::SetXVar(const Wml::GVectord& x)
{
	X = x[0];
	Y = x[1];
	Z = x[2];
}

void ZReprojective_LinearExpress::SetP(Wml::Matrix4d P)
{
	P00 = P(0,0);	P01 = P(0,1);	P02 = P(0,2);	P03 = P(0,3);
	P10 = P(1,0);	P11 = P(1,1);	P12 = P(1,2);	P13 = P(1,3);
	P20 = P(2,0);	P21 = P(2,1);	P22 = P(2,2);	P23 = P(2,3);	
}

double ZReprojective_LinearExpress::Value (const Wml::GVectord& x)
{
	double value=0;
	SetXVar(x);
	for(int i=0;i<m_points.size();i++){
		u1=m_points[i].X();
		v1=m_points[i].Y();
		w = wList[i];
		SetP(PList[i]);
		
		value += PowN((P00*X+P01*Y+P02*Z+P03-u1*(P20*X+P21*Y+P22*Z+P23)),2)+PowN((P10*X+P11*Y+P12*Z+P13-v1*(P20*X+P21*Y+P22*Z+P23)),2); 
	}

	return value*w*w;
}

void ZReprojective_LinearExpress::Gradient (const Wml::GVectord& x, Wml::GVectord& grad)
{
	int i,j;
	SetXVar(x);
	if( grad.GetSize() != m_varIndex.size() )
		grad.SetSize( m_varIndex.size() );
	
	for(i=0;i<m_varIndex.size();i++){
		grad[i]=0.0;
	}
	
	for(j=0;j<m_points.size();j++){
		u1=m_points[j].X();
		v1=m_points[j].Y();
		w = wList[j];
		SetP(PList[j]);

		for(i=0;i<m_varIndex.size();i++) 
		{
			switch (i)
			{
			case 0:
				grad[i] += w*w * ( 2*(P00*X+P01*Y+P02*Z+P03-u1*(P20*X+P21*Y+P22*Z+P23))*(P00-u1*P20)+2*(P10*X+P11*Y+P12*Z+P13-v1*(P20*X+P21*Y+P22*Z+P23))*(P10-v1*P20) );
				break;
			case 1:
				grad[i] += w*w * ( 2*(P00*X+P01*Y+P02*Z+P03-u1*(P20*X+P21*Y+P22*Z+P23))*(P01-u1*P21)+2*(P10*X+P11*Y+P12*Z+P13-v1*(P20*X+P21*Y+P22*Z+P23))*(P11-v1*P21) );
				break;
			case 2:
				grad[i] += w*w * ( 2*(P00*X+P01*Y+P02*Z+P03-u1*(P20*X+P21*Y+P22*Z+P23))*(P02-u1*P22)+2*(P10*X+P11*Y+P12*Z+P13-v1*(P20*X+P21*Y+P22*Z+P23))*(P12-v1*P22) );
				break;
			}//end of switch(i)
		}//end of for
	}
}

void ZReprojective_LinearExpress::Hesse (const Wml::GVectord& x, Wml::GMatrixd& hesse)
{
	int i,j,k;
	SetXVar(x);
	for(i=0;i<m_varIndex.size();i++){
		for(j=0;j<m_varIndex.size();j++)
			hesse(i,j)=0.0;
	}
	
	for(k=0;k<m_points.size();k++){
		u1=m_points[k].X();
		v1=m_points[k].Y();
		w = wList[k];
		SetP(PList[k]);
	
	for( int i=0;i<m_varIndex.size();i++)
		for ( int j=i;j<m_varIndex.size();j++)
	{
		switch (i)
		{
		case 0:
			switch(j)
			{
			case 0:
				hesse(i,j) += w*w * ( 2*PowN((P00-u1*P20),2)+2*PowN((P10-v1*P20),2) );
				break;
			case 1:
				hesse(i,j) += w*w * ( 2*(P00-u1*P20)*(P01-u1*P21)+2*(P10-v1*P20)*(P11-v1*P21) );
				break;
			case 2:
				hesse(i,j) += w*w * ( 2*(P00-u1*P20)*(P02-u1*P22)+2*(P10-v1*P20)*(P12-v1*P22) );
				break;
			}//end of switch(j)
			break;
		case 1:
			switch(j)
			{
			case 1:
				hesse(i,j) += w*w * ( 2*PowN((P01-u1*P21),2)+2*PowN((P11-v1*P21),2) );
				break;
			case 2:
				hesse(i,j) += w*w * ( 2*(P01-u1*P21)*(P02-u1*P22)+2*(P11-v1*P21)*(P12-v1*P22) );
				break;
			}//end of switch(j)
			break;
		case 2:
			switch(j)
			{
			case 2:
				hesse(i,j) += w*w * ( 2*PowN((P02-u1*P22),2)+2*PowN((P12-v1*P22),2) );
				break;
			}//end of switch(j)
			break;
		}//end of switch(i)
		hesse(j,i)=hesse(i,j);
	}//end of for
	}
	
}
