// ImageMatchHomoModel.h: interface for the ImageMatchHomoModel class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_IMAGEMATCHHOMOMODEL_H__B9A192FD_56A5_4E2E_A7CE_24BA6BC7CD85__INCLUDED_)
#define AFX_IMAGEMATCHHOMOMODEL_H__B9A192FD_56A5_4E2E_A7CE_24BA6BC7CD85__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "HomoTransform.h"
#include "IRANSACModel.h"

#include "globalMath.h"

//#include "VideoFrame.h"
//#include "MatchLinker.h"

class ImageMatchHomoModel : public IRANSACModel  
{
public:
	ImageMatchHomoModel();
	virtual ~ImageMatchHomoModel();

	ImageMatchHomoModel (double fitThresh, double distanceFactor,
		double iWidth, double iHeight)
	{
		this->fitThresh = fitThresh;
		this->distanceFactor = distanceFactor;
		this->m_iWidth = iWidth;
		this->m_iHeight = iHeight;
	}

	IRANSACModel* Clone()
	{
		ImageMatchHomoModel* mod = new ImageMatchHomoModel (fitThresh,
			distanceFactor, m_iWidth, m_iHeight);

		mod->trans = trans;

		return (mod);
	}

	bool FitModel(SingleList& singleList)
	{
		if (singleList.size() < 4) {
			//Console.WriteLine ("ImageMatchModel.FitModel: Need at least two matches to fit.");

			return (false);
		}

		// TODO: least-square match if more than two points are given.
		// For now, just ignore any further matches.	
		//Console.WriteLine ("Doing transform building...");

		//trans = HomoTransform::BuildTransform(singleList);
		if(singleList.size()==4)
			trans = HomoTransform::BuildTransform(singleList);

		else
			HomoTransform::RefineTransform(singleList,trans);
	
		return (true);
	}

	double FittingErrorSingle (MatchPoint* p)
	{
		Wml::Vector3d Xexpected = trans * p->v_3d;

		Xexpected[0] /= Xexpected[2];
		Xexpected[1] /= Xexpected[2];
		Xexpected[2] = 1.0;

		double pixDistance = sqrt(pow2(p->v_2d[0] - Xexpected[0])+pow2(p->v_2d[1] - Xexpected[1]));

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

	void RemoveErrorPoint1(SingleList& singleList,double thresholdN=4.0,double percent=0.9);
	void RemoveErrorPoint(SingleList& singleList, double threshold = 2.0);

	HomoTransform trans;

	double fitThresh;

	// The distance-gratifying factor in the distance relaxing formula.
	double distanceFactor;
	// The image resolution to calculate the maximum possible distance.
	double m_iWidth, m_iHeight;
};

#endif // !defined(AFX_IMAGEMATCHHOMOMODEL_H__B9A192FD_56A5_4E2E_A7CE_24BA6BC7CD85__INCLUDED_)
