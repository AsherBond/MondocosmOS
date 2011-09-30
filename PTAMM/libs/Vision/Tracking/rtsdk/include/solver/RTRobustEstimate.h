// RTRobustEstimate.h: interface for the RTRobustEstimate class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_RTROBUSTESTIMATE_H__E1A8BE08_799B_4931_B584_1F92464C9A56__INCLUDED_)
#define AFX_RTROBUSTESTIMATE_H__E1A8BE08_799B_4931_B584_1F92464C9A56__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "IRANSACModel.h"
#include "ZRTSolution_Known3DExpress.h"
#include <algorithm>
#include "ZOptimizerLM.h"
#include "globalMath.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Solver
/// \brief Estimate the rotation and translate matrix with the good points.
//////////////////////////////////////////////////////////////////////////
template<class T>
class RTRobustEstimate  
{
public:
	RTRobustEstimate();
	virtual ~RTRobustEstimate();

	/// \brief Robust estimate the R and t.
	void RobustEstimate(SingleList& points,Wml::GVector<T>& x, int n, T threshold, int iterCount);

	/// \brief Estimate the R and t with all the points.
	void Estimate(SingleList& points, Wml::GVector<T>& x, Wml::GVector<T>& x2,int iterCount);

	//////////////////////////////////////////////////////////////////////////
	T FittingErrorSingle(MatchPoint* pt);

	//////////////////////////////////////////////////////////////////////////
	void SetXVar(const Wml::GVector<T>& x);

private:
	T alpha,beta,gama,t0,t1,t2;
	T sinAlpha,cosAlpha,sinBeta,cosBeta,sinGama,cosGama;
	T X,Y,Z;
	T u1,v1;
};

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////
template <class T>
RTRobustEstimate<T>::RTRobustEstimate()
{
	
}

template <class T>
RTRobustEstimate<T>::~RTRobustEstimate()
{
	
}

//////////////////////////////////////////////////////////////////////////
/// \brief Fit a camera model on the points.
///
//////////////////////////////////////////////////////////////////////////
template<class T>
void RTRobustEstimate<T>::Estimate(SingleList& points, Wml::GVector<T>& x, Wml::GVector<T>& x2,int iterCount)
{
	T	MinValue=0;
 	ZOptimizerLM<T>*	m_pOptimizerLM = new ZOptimizerLM<T>;
 	
	//3.数值求导算法
 	ZRTSolution_Known3DExpress<T> m_fundMExpress;

	for(int i=0; i<points.size(); i++){
		m_fundMExpress.ReadPoint(*points.at(i));
	}
	
	if(m_fundMExpress.Value(x2)<m_fundMExpress.Value(x))
		x = x2;
	
	m_pOptimizerLM->SetMaxIteration(iterCount);

	m_pOptimizerLM->Optimize(m_fundMExpress,x,MinValue);	


	delete m_pOptimizerLM;
}

//////////////////////////////////////////////////////////////////////////
///
/// 
/// \param points [in|out] The matched keypoints.
/// \param x      [in|out] The camera parameters, x[6], 3 for rotation, 3 for translation.
/// \param n      [in] The number of the sampled points.
/// \param threshold
/// \param iterCount
//////////////////////////////////////////////////////////////////////////
template<class T>
void RTRobustEstimate<T>::RobustEstimate(SingleList& points,Wml::GVector<T>& x, int n, T threshold, int iterCount)
{
	// The new estimated x.
	Wml::GVector<T> bestx = x;

	// Good points list.
	SingleList goodList;

	// Sum of fitting error of all good points.
	double overAllFittingError = 1e100;
	
	// Random seeds for the random sampling.
	srand( (unsigned)time( NULL ) );
	
#ifdef _DEBUG
	printf("RobustEstimate!\n");
#endif

	for (int ki = 0 ; ki < iterCount ; ++ki) {
		
		//printf("iter = %d\n", ki);

		SingleList samples;
		std::vector<int> sampleindex;
		
		// Build random samples
		for (int ri = 0 ; ri < n ; )
		{
			// Random index.
			int randIndex = rand()%points.size();
			
			if (std::find(sampleindex.begin(),sampleindex.end(),randIndex) != sampleindex.end())
				continue;
			
			// New sample.
			++ri;

			samples.push_back (points[randIndex]);
			sampleindex.push_back(randIndex);
		}
		
		//estimate it!
		Wml::GVector<T> tmpx = x;
		Estimate(samples,tmpx,bestx,30);
		
		//////////////////////////////////////////////////////////////////////////
		SetXVar(tmpx);

		//check the good points!
		SingleList::iterator iterPoint;
		SingleList tmpGoodList;

		double tmpAllFittingError = 0.0;
		for(iterPoint = points.begin();iterPoint!=points.end();iterPoint++)
		{
			double fitError = FittingErrorSingle (*iterPoint);
		
			//printf("%f\t",fitError/threshold*3.0);
			if (fitError<threshold) {
				tmpGoodList.push_back(*iterPoint);
				tmpAllFittingError += fitError;
			}			
		}
		
		if(tmpGoodList.size() > goodList.size())
		{
			// If current fitted model matches the points better than the previous.
			goodList = tmpGoodList;
			overAllFittingError = tmpAllFittingError;
			bestx = tmpx;

			// If the fitted model matches most of the points.
			if(goodList.size() >= points.size()*0.95)
				goto FINAL;
		}
		else if(tmpGoodList.size() == goodList.size()&&tmpAllFittingError<overAllFittingError)
		{
			// If current fitted model matches the points as the previous, compare the fitting error.
			goodList = tmpGoodList;
			overAllFittingError = tmpAllFittingError;
			bestx = tmpx;
		}
	}
	

#ifdef _DEBUG
	printf("total points:%d, good points:%d\n", points.size(), goodList.size());
#endif
	
FINAL:
	if(goodList.size() > n)
	{
		Estimate(goodList, bestx, bestx, 30);

		x = bestx;

		// Do not Delete the outliers! Will Release all points together at the end!!!
		points = goodList;
	}
}


//////////////////////////////////////////////////////////////////////////
/// \brief Set the camera model.
///
/// \param x [in] The camera parameters.
//////////////////////////////////////////////////////////////////////////
template <class T>
void RTRobustEstimate<T>::SetXVar(const Wml::GVector<T>& x)
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

//////////////////////////////////////////////////////////////////////////
/// \brief The fitting error of a point.
///
/// \param pt [in] The point.
/// \return The error.
//////////////////////////////////////////////////////////////////////////
template <class T>
T RTRobustEstimate<T>::FittingErrorSingle(MatchPoint* pt)
{
	T val;
	
	u1=pt->v_2d.X();
	v1=pt->v_2d.Y();
	X=pt->v_3d.X();
	Y=pt->v_3d.Y();
	Z=pt->v_3d.Z();
	
	val = PowN(((cosGama*cosBeta*X+(-sinGama*cosAlpha+cosGama*sinBeta*sinAlpha)*Y+(sinGama*sinAlpha+cosGama*sinBeta*cosAlpha)*Z+t0)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-u1),2)+PowN(((sinGama*cosBeta*X+(cosGama*cosAlpha+sinGama*sinBeta*sinAlpha)*Y+
		(-cosGama*sinAlpha+sinGama*sinBeta*cosAlpha)*Z+t1)/(-sinBeta*X+cosBeta*sinAlpha*Y+cosBeta*cosAlpha*Z+t2)-v1),2); 
	val = sqrt(val);
	
	return val;
}

#endif // !defined(AFX_RTROBUSTESTIMATE_H__E1A8BE08_799B_4931_B584_1F92464C9A56__INCLUDED_)
