// ZRTSolution_Known3DExpress.h: interface for the ZRTSolution_Known3DExpress class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_ZRTSOLUTION_KNOWN3DEXPRESS_H__2767EAB1_C5AE_43B1_B248_81F2CC6EB5D6__INCLUDED_)
#define AFX_ZRTSOLUTION_KNOWN3DEXPRESS_H__2767EAB1_C5AE_43B1_B248_81F2CC6EB5D6__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
#include "ZAnyScaleFunction.h"
#include "IRANSACModel.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Solver
/// \brief Expression of known 3D structure.
//////////////////////////////////////////////////////////////////////////
template <class T>
class ZRTSolution_Known3DExpress : public ZAnyScaleFunction<T>  
{
public:
	ZRTSolution_Known3DExpress();
	virtual ~ZRTSolution_Known3DExpress();

	/// \brief The value of the expression.
	virtual T Value (const Wml::GVector<T>& x);
	
	/// \brief The Jacobian matrix.
	virtual void Gradient (const Wml::GVector<T>& x, Wml::GVector<T>& grad);
	
	/// \brief The Hessian matrix.
	virtual void Hesse (const Wml::GVector<T>& x, Wml::GMatrix<T>& hesse);

	/// \brief Set the parameters of the expression.
	void SetXVar(const Wml::GVector<T>& x);
	
	/// \brief Set the point samples.
	void ReadPoint(MatchPoint& pt);
	
	/// \brief Print out the x parameters.
	void PrintVector(const Wml::GVector<T>& x);
	
	/// \brief The number of sample points.
	int GetPointNumber()
	{
		return m_points.size();
	}

protected:
	//////////////////////////////////////////////////////////////////////////
	/// \brief The camera parameters.
	///
	/// alpha,beta,gama for rotation, and t0,t1,t2 for translation.
	//////////////////////////////////////////////////////////////////////////
	T alpha,beta,gama,t0,t1,t2;
	
	//////////////////////////////////////////////////////////////////////////
	/// \brief Precomputed cosin and sine function.
	///
	/// For performance consideration.
	//////////////////////////////////////////////////////////////////////////
	T sinAlpha,cosAlpha,sinBeta,cosBeta,sinGama,cosGama;
	
	//////////////////////////////////////////////////////////////////////////
	/// \brief The 3D coordinates.
	//////////////////////////////////////////////////////////////////////////
	T X,Y,Z;

	//////////////////////////////////////////////////////////////////////////
	/// \brief The 2D image coordinates.
	//////////////////////////////////////////////////////////////////////////
	T u1,v1;

	//////////////////////////////////////////////////////////////////////////
	/// \brief Variable index.
	//////////////////////////////////////////////////////////////////////////
	std::vector<int> m_LocalVariableIndex;

	//////////////////////////////////////////////////////////////////////////
	/// \brief The sampled points.
	//////////////////////////////////////////////////////////////////////////
	std::vector<MatchPoint*>	m_points;
};

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

template <class T>
ZRTSolution_Known3DExpress<T>::ZRTSolution_Known3DExpress()
{
	for(int i=0;i<6;i++)
		m_LocalVariableIndex.push_back(i);
}

template <class T>
ZRTSolution_Known3DExpress<T>::~ZRTSolution_Known3DExpress()
{

}

template <class T>
void ZRTSolution_Known3DExpress<T>::SetXVar(const Wml::GVector<T>& x)
{
	alpha = x[0];
	beta =x[1];
	gama = x[2];
	t0 = x[3];
	t1 = x[4];
	t2=x[5];

	sinAlpha = sin(alpha);
	cosAlpha = cos(alpha);
	sinBeta = sin(beta);
	cosBeta = cos(beta);
	sinGama = sin(gama);
	cosGama = cos(gama);
}

template <class T>
void ZRTSolution_Known3DExpress<T>::PrintVector(const Wml::GVector<T>& x)
{
	int i;
	for(i=0;i<x.GetSize();i++){
		printf("%f ",x[i]);
	}
	printf("\n");
}

template <class T>
void ZRTSolution_Known3DExpress<T>::ReadPoint(MatchPoint& p)
{
	m_points.push_back(&p);
}

template <class T>
T ZRTSolution_Known3DExpress<T>::Value (const Wml::GVector<T>& x)
{
	T sum=0;
	//double bsum=0;
	//double SinAlpha=0,CosAlpha=1,SinBeta=0,CosBeta=1,SinGama=0,CosGama=1;
	SetXVar(x);

	//PrintVector(x);
	for(int i=0;i<m_points.size();i++)
	{
		u1=m_points[i]->v_2d.X();
		v1=m_points[i]->v_2d.Y();
		X=m_points[i]->v_3d.X();
		Y=m_points[i]->v_3d.Y();
		Z=m_points[i]->v_3d.Z();
		
		sum+=PowN(((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1),2)+PowN(((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+
			(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1),2); 
	}
	//printf("sum=%lf\n",sum);
	return sum;
}

template <class T>
void ZRTSolution_Known3DExpress<T>::Gradient (const Wml::GVector<T>& x, Wml::GVector<T>& grad)
{
	int i,j;
	SetXVar(x);
	if( grad.GetSize() != m_LocalVariableIndex.size() )
		grad.SetSize( m_LocalVariableIndex.size() );
	
	for(i=0;i<m_LocalVariableIndex.size();i++){
		grad[i]=0.0;
	}
	
	for(j=0;j<m_points.size();j++){
		u1=m_points[j]->v_2d.X();
		v1=m_points[j]->v_2d.Y();
		X=m_points[j]->v_3d.X();
		Y=m_points[j]->v_3d.Y();
		Z=m_points[j]->v_3d.Z();

		for(i=0;i<m_LocalVariableIndex.size();i++) 
		{
			switch (m_LocalVariableIndex[i])
			{
			case 0:
				grad[i]+=2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)*(((sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Y+(sinGama*cosAlpha-
					cosGama*sinBeta*sinAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+
					t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z))+2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)*(((-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Y+
					(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+
					cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z));
				break;
			case 1:
				grad[i]+=2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)*((-cosGama*sinBeta*X+cosGama*cosBeta*sinAlpha*Y+cosGama*cosBeta*cosAlpha*Z)/(-
					sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-
					sinBeta*cosAlpha*Z))+2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)*((-sinGama*sinBeta*X+sinGama*cosBeta*sinAlpha*Y+sinGama*cosBeta*cosAlpha*Z)/(-
					sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-
					sinBeta*cosAlpha*Z));
				break;
			case 2:
				grad[i]+=2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)*(-sinGama*cosBeta*X+(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Y+
					(cosGama*sinAlpha-sinGama*sinBeta*cosAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)+2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+
					cosBeta*cosAlpha*Z+t2)-v1)*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2);
				break;
			case 3:
				grad[i]+=2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2);
				break;
			case 4:
				grad[i]+=2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2);
				break;
			case 5:
				grad[i]+=-2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+
					(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)-2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+
					cosBeta*cosAlpha*Z+t2)-v1)*(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2);
				break;
			}//end of switch(i)
		}//end of for
	}
}

template <class T>
void ZRTSolution_Known3DExpress<T>::Hesse (const Wml::GVector<T>& x, Wml::GMatrix<T>& hesse)
{
	int i,j,k;
	SetXVar(x);
//	if( hesse.GetSize()!=m_LocalVariableIndex.size() )
//		hesse.SetSize(m_LocalVariableIndex.size());
	
	for(i=0;i<m_LocalVariableIndex.size();i++){
		for(j=0;j<m_LocalVariableIndex.size();j++)
			hesse(i,j)=0.0;
	}
	
	for(k=0;k<m_points.size();k++){
		u1=m_points[k]->v_2d.X();
		v1=m_points[k]->v_2d.Y();
		X=m_points[k]->v_3d.X();
		Y=m_points[k]->v_3d.Y();
		Z=m_points[k]->v_3d.Z();

	for( int i=0;i<m_LocalVariableIndex.size();i++)
		for ( int j=i;j<m_LocalVariableIndex.size();j++)
	{
		switch (m_LocalVariableIndex[i])
		{
		case 0:
			switch(m_LocalVariableIndex[j])
			{
			case 0:
				hesse(i,j)+=2*PowN((((sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Y+(sinGama*cosAlpha-cosGama*sinBeta*sinAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+
			cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z)),2)+2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+
			t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)*(((sinGama*cosAlpha-cosGama*sinBeta*sinAlpha)*Y+(-sinGama*sinAlpha-cosGama*sinBeta*cosAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-2*((sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Y+
			(sinGama*cosAlpha-cosGama*sinBeta*sinAlpha)*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z)+2*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+
			t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),3)*PowN((cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z),2)-(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+
			cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*sinAlpha*Y-cosBeta*cosAlpha*Z))+2*PowN((((-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Y+(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(sinGama*cosBeta*X+(cosGama*cosAlpha+
			sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z)),2)+2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+
			(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)*(((-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Y+(cosGama*sinAlpha-sinGama*sinBeta*cosAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+
			t2)-2*((-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Y+(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z)+2*(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+
			(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),3)*PowN((cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z),2)-(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+
			t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*sinAlpha*Y-cosBeta*cosAlpha*Z));
				break;
			case 1:
				hesse(i,j)+=2*(((sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Y+(sinGama*cosAlpha-cosGama*sinBeta*sinAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+
			cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z))*((-cosGama*sinBeta*X+cosGama*cosBeta*sinAlpha*Y+cosGama*cosBeta*cosAlpha*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+
			t2)-(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z))+2*((cosGama*cosBeta*X+
			(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)*((cosGama*cosBeta*cosAlpha*Y-cosGama*cosBeta*sinAlpha*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+
			t2)-(-cosGama*sinBeta*X+cosGama*cosBeta*sinAlpha*Y+cosGama*cosBeta*cosAlpha*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z)-((sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Y+(sinGama*cosAlpha-cosGama*sinBeta*sinAlpha)*Z)/PowN((-
			sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z)+2*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+
			cosBeta*cosAlpha*Z+t2),3)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z)-(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+
			cosBeta*cosAlpha*Z+t2),2)*(-sinBeta*cosAlpha*Y+sinBeta*sinAlpha*Z))+2*(((-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Y+(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(sinGama*cosBeta*X+(cosGama*cosAlpha+
			sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z))*((-sinGama*sinBeta*X+sinGama*cosBeta*sinAlpha*Y+sinGama*cosBeta*cosAlpha*Z)/(-
			sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-
			sinBeta*cosAlpha*Z))+2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)*((sinGama*cosBeta*cosAlpha*Y-sinGama*cosBeta*sinAlpha*Z)/(-
			sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(-sinGama*sinBeta*X+sinGama*cosBeta*sinAlpha*Y+sinGama*cosBeta*cosAlpha*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z)-((-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Y+
			(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z)+2*(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+
			t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),3)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z)-(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+
			t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-sinBeta*cosAlpha*Y+sinBeta*sinAlpha*Z));
				break;
			case 2:
				hesse(i,j)+=2*(((sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Y+(sinGama*cosAlpha-cosGama*sinBeta*sinAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+
			cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z))*(-sinGama*cosBeta*X+(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Y+(cosGama*sinAlpha-sinGama*sinBeta*cosAlpha)*Z)/(-sinBeta*X+
			cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)+2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)*((cosGama*sinAlpha-sinGama*sinBeta*cosAlpha)*Y+
			(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+
			cosBeta*cosAlpha*Z+t2)-u1)*(-sinGama*cosBeta*X+(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Y+(cosGama*sinAlpha-sinGama*sinBeta*cosAlpha)*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z)+2*(((-cosGama*sinAlpha+
			sinGama*sinBeta*cosAlpha)*Y+(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-
			sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z))*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+
			t2)+2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)*((sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Y+(sinGama*cosAlpha-cosGama*sinBeta*sinAlpha)*Z)/(-
			sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)*(cosGama*cosBeta*X+(-sinGama*cosAlpha+
			cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z);
				break;
			case 3:
				hesse(i,j)+=2*(((sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Y+(sinGama*cosAlpha-cosGama*sinBeta*sinAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+
			cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z))/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+
			(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z);
				break;
			case 4:
				hesse(i,j)+=2*(((-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Y+(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+
			sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z))/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+
			(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z);
				break;
			case 5:
				hesse(i,j)+=-2*(((sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Y+(sinGama*cosAlpha-cosGama*sinBeta*sinAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+
			cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z))*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-
			sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)-2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)*((sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Y+
			(sinGama*cosAlpha-cosGama*sinBeta*sinAlpha)*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)+4*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+
			cosBeta*cosAlpha*Z+t2)-u1)*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),3)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z)-2*(((-cosGama*sinAlpha+
			sinGama*sinBeta*cosAlpha)*Y+(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-
			sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z))*(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+
			t2),2)-2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)*((-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Y+(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Z)/PowN((-
			sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)+4*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)*(sinGama*cosBeta*X+(cosGama*cosAlpha+
			sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),3)*(cosBeta*cosAlpha*Y-cosBeta*sinAlpha*Z);
				break;
			}//end of switch(j)
			break;
		case 1:
			switch(m_LocalVariableIndex[j])
			{
			case 1:
				hesse(i,j)+=2*PowN(((-cosGama*sinBeta*X+cosGama*cosBeta*sinAlpha*Y+cosGama*cosBeta*cosAlpha*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+
			t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z)),2)+2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+
			cosBeta*cosAlpha*Z+t2)-u1)*((-cosGama*cosBeta*X-cosGama*sinBeta*sinAlpha*Y-cosGama*sinBeta*cosAlpha*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-2*(-cosGama*sinBeta*X+cosGama*cosBeta*sinAlpha*Y+cosGama*cosBeta*cosAlpha*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+
			cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z)+2*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),3)*PowN((-
			cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z),2)-(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(sinBeta*X-cosBeta*sinAlpha*Y-
			cosBeta*cosAlpha*Z))+2*PowN(((-sinGama*sinBeta*X+sinGama*cosBeta*sinAlpha*Y+sinGama*cosBeta*cosAlpha*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+
			t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z)),2)+2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+
			cosBeta*cosAlpha*Z+t2)-v1)*((-sinGama*cosBeta*X-sinGama*sinBeta*sinAlpha*Y-sinGama*sinBeta*cosAlpha*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-2*(-sinGama*sinBeta*X+sinGama*cosBeta*sinAlpha*Y+sinGama*cosBeta*cosAlpha*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+
			cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z)+2*(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),3)*PowN((-
			cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z),2)-(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(sinBeta*X-cosBeta*sinAlpha*Y-
			cosBeta*cosAlpha*Z));
				break;
			case 2:
				hesse(i,j)+=2*((-cosGama*sinBeta*X+cosGama*cosBeta*sinAlpha*Y+cosGama*cosBeta*cosAlpha*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+
			t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z))*(-sinGama*cosBeta*X+(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Y+(cosGama*sinAlpha-sinGama*sinBeta*cosAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+
			cosBeta*cosAlpha*Z+t2)+2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)*(sinGama*sinBeta*X-sinGama*cosBeta*sinAlpha*Y-sinGama*cosBeta*cosAlpha*Z)/(-
			sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)*(-sinGama*cosBeta*X+(-cosGama*cosAlpha-
			sinGama*sinBeta*sinAlpha)*Y+(cosGama*sinAlpha-sinGama*sinBeta*cosAlpha)*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z)+2*((-sinGama*sinBeta*X+sinGama*cosBeta*sinAlpha*Y+sinGama*cosBeta*cosAlpha*Z)/(-
			sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-
			sinBeta*cosAlpha*Z))*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)+2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+
			(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)*(-cosGama*sinBeta*X+cosGama*cosBeta*sinAlpha*Y+cosGama*cosBeta*cosAlpha*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-2*((sinGama*cosBeta*X+
			(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z)/PowN((-
			sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z);
				break;
			case 3:
				hesse(i,j)+=2*((-cosGama*sinBeta*X+cosGama*cosBeta*sinAlpha*Y+cosGama*cosBeta*cosAlpha*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+
			t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z))/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+
			cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z);
				break;
			case 4:
				hesse(i,j)+=2*((-sinGama*sinBeta*X+sinGama*cosBeta*sinAlpha*Y+sinGama*cosBeta*cosAlpha*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+
			t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z))/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+
			sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z);
				break;
			case 5:
				hesse(i,j)+=-2*((-cosGama*sinBeta*X+cosGama*cosBeta*sinAlpha*Y+cosGama*cosBeta*cosAlpha*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+
			t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z))*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+
			cosBeta*cosAlpha*Z+t2),2)-2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)*(-cosGama*sinBeta*X+cosGama*cosBeta*sinAlpha*Y+cosGama*cosBeta*cosAlpha*Z)/PowN((-
			sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)+4*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)*(cosGama*cosBeta*X+(-
			sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),3)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z)-2*((-sinGama*sinBeta*X+sinGama*cosBeta*sinAlpha*Y+
			sinGama*cosBeta*cosAlpha*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+
			t2),2)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z))*(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)-2*((sinGama*cosBeta*X+
			(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)*(-sinGama*sinBeta*X+sinGama*cosBeta*sinAlpha*Y+sinGama*cosBeta*cosAlpha*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+
			cosBeta*cosAlpha*Z+t2),2)+4*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)*(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+
			(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),3)*(-cosBeta*X-sinBeta*sinAlpha*Y-sinBeta*cosAlpha*Z);
				break;
			}//end of switch(j)
			break;
		case 2:
			switch(m_LocalVariableIndex[j])
			{
			case 2:
				hesse(i,j)+=2*PowN((-sinGama*cosBeta*X+(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Y+(cosGama*sinAlpha-sinGama*sinBeta*cosAlpha)*Z),2)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)+2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+
			(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)*(-cosGama*cosBeta*X+(sinGama*cosAlpha-cosGama*sinBeta*sinAlpha)*Y+(-sinGama*sinAlpha-cosGama*sinBeta*cosAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+
			cosBeta*cosAlpha*Z+t2)+2*PowN((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z),2)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)+2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+
			(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)*(-sinGama*cosBeta*X+(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Y+(cosGama*sinAlpha-sinGama*sinBeta*cosAlpha)*Z)/(-sinBeta*X+cosBeta*sinAlpha*Y+
			cosBeta*cosAlpha*Z+t2);
				break;
			case 3:
				hesse(i,j)+=2*(-sinGama*cosBeta*X+(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Y+(cosGama*sinAlpha-sinGama*sinBeta*cosAlpha)*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2);
				break;
			case 4:
				hesse(i,j)+=2*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2);
				break;
			case 5:
				hesse(i,j)+=-2*(-sinGama*cosBeta*X+(-cosGama*cosAlpha-sinGama*sinBeta*sinAlpha)*Y+(cosGama*sinAlpha-sinGama*sinBeta*cosAlpha)*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),3)*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+
			(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)-2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)*(-sinGama*cosBeta*X+(-cosGama*cosAlpha-
			sinGama*sinBeta*sinAlpha)*Y+(cosGama*sinAlpha-sinGama*sinBeta*cosAlpha)*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2)-2*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z)/PowN((-
			sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),3)*(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)-2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+
			sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+
			t2),2);
				break;
			}//end of switch(j)
			break;
		case 3:
			switch(m_LocalVariableIndex[j])
			{
			case 3:
				hesse(i,j)+=2/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2);
				break;
			case 4:
				hesse(i,j)+=0;
				break;
			case 5:
				hesse(i,j)+=-2/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),3)*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)-2*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+
			(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2);
				break;
			}//end of switch(j)
			break;
		case 4:
			switch(m_LocalVariableIndex[j])
			{
			case 4:
				hesse(i,j)+=2/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2);
				break;
			case 5:
				hesse(i,j)+=-2/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),3)*(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)-2*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+
			(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),2);
				break;
			}//end of switch(j)
			break;
		case 5:
			switch(m_LocalVariableIndex[j])
			{
			case 5:
				hesse(i,j)+=2*PowN((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0),2)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),4)+4*((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+
			(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1)*(cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+
			cosBeta*cosAlpha*Z+t2),3)+2*PowN((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1),2)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2),4)+4*((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+
			(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1)*(sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/PowN((-sinBeta*X+cosBeta*sinAlpha*Y+
			cosBeta*cosAlpha*Z+t2),3);
				break;
			}//end of switch(j)
			break;
		}//end of switch(i)
		hesse(j,i)=hesse(i,j);
	}//end of for
	}
	
}


#endif // !defined(AFX_ZRTSOLUTION_KNOWN3DEXPRESS_H__2767EAB1_C5AE_43B1_B248_81F2CC6EB5D6__INCLUDED_)
