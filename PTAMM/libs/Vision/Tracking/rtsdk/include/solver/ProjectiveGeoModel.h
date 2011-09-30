// ProjectiveGeoModel.h: interface for the ProjectiveGeoModel class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_PROJECTIVEGEOMODEL_H__86BF9DDD_80E0_4B5F_93ED_11ADCF22F9E7__INCLUDED_)
#define AFX_PROJECTIVEGEOMODEL_H__86BF9DDD_80E0_4B5F_93ED_11ADCF22F9E7__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "IRANSACModel.h"
#include "ProjectiveMatirx.h"
#include "globalMath.h"

class ProjectiveGeoModel : public IRANSACModel  
{
public:
	ProjectiveGeoModel();
	virtual ~ProjectiveGeoModel();

	ProjectiveGeoModel (double fitThresh, double distanceFactor,
		double fx, double fy)
	{
		this->fitThresh = fitThresh;
		this->distanceFactor = distanceFactor;
		this->m_fx = fx;
		this->m_fy = fy;
	}

	IRANSACModel* Clone()
	{
		ProjectiveGeoModel* mod = new ProjectiveGeoModel (fitThresh,
			distanceFactor, m_fx, m_fy);

		mod->trans = trans;

		return (mod);
	}

	bool FitModel(SingleList& singleList)
	{
		if (singleList.size() < 6) {			
			return (false);
		}

		// TODO: least-square match if more than two points are given.
		// For now, just ignore any further matches.	
		//Console.WriteLine ("Doing transform building...");
		else if(singleList.size() == 6){
			trans = ProjectiveMatirx::BuildTransform(singleList);
		}
		else 
			ProjectiveMatirx::RefineTransform(singleList,trans);
		return (true);
	}

	double FittingErrorSingle (MatchPoint* p)
	{
		double pixDistance;
		Wml::Vector3d pt = trans.Multiply(p->v_3d);
		pt[0] /= pt[2];
		pt[1] /= pt[2];
		pt[2] = 1.0;
		
		pixDistance = sqrt(PowN(m_fx*(pt[0]-p->v_2d.X()),2) + PowN(m_fy*(pt[1]-p->v_2d.Y()),2));

		return pixDistance;
	}

	bool ThreshholdPoint (double fitError)
	{
		/*Console.WriteLine ("TreshholdPoint: {0} to {1} # RANSACTHRESH",
			fitError, fitThresh);*/
		if (fitError > fitThresh)
			return (false);

		return (true);
	}

	ProjectiveMatirx trans;

	double fitThresh;

	// The distance-gratifying factor in the distance relaxing formula.
	double distanceFactor;
	// The focal length.
	double m_fx, m_fy;
};

#endif // !defined(AFX_PROJECTIVEGEOMODEL_H__86BF9DDD_80E0_4B5F_93ED_11ADCF22F9E7__INCLUDED_)
