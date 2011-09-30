// Estimate3DStructure.cpp: implementation of the Estimate3DStructure class.
//
//////////////////////////////////////////////////////////////////////

#include "Estimate3DStructure.h"
#include "ZReprojective_LinearExpress.h"
#include "ZReprojective_NonLinearExpress.h"
#include "ZOptimizerLM.h"
#include "globalMath.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

double Pow2(double a)
{
	return a*a;
}

double Track_RMSE(Wml::Vector2d &point_2d, Wml::Matrix4d &P, 
				  Wml::Matrix3d &K,
				  Wml::Vector3d &v_3d)
{
	Wml::Vector3d pt3d;

	pt3d[0] = P(0,0)*v_3d[0]+P(0,1)*v_3d[1]+P(0,2)*v_3d[2]+P(0,3);
	pt3d[1] = P(1,0)*v_3d[0]+P(1,1)*v_3d[1]+P(1,2)*v_3d[2]+P(1,3);
	pt3d[2] = P(2,0)*v_3d[0]+P(2,1)*v_3d[1]+P(2,2)*v_3d[2]+P(2,3);
	pt3d[0] /= pt3d[2];
	pt3d[1] /= pt3d[2];
	pt3d[2] = 1.0;


	double RMSE = Pow2(K(0, 0)*pt3d[0] + K(0, 2) - point_2d.X()) + Pow2(K(1, 1) * pt3d[1] + K(1, 2) - point_2d.Y());
	

	RMSE = sqrt(RMSE);

	return RMSE;
}

double Track_RMSE(std::vector<Wml::Vector2d> &points_2d,
				  std::vector<Wml::Matrix4d> &cameras, 
				  std::vector<Wml::Matrix3d> &Ks,
				  Wml::Vector3d &v_3d)
{
	int iCount = points_2d.size();

	double RMSE = 0;
	
	for(int i = 0; i < iCount; ++ i)
	{
		Wml::Matrix4d& P = cameras[i];
	
		Wml::Vector3d pt3d;

		pt3d[0] = P(0,0)*v_3d[0]+P(0,1)*v_3d[1]+P(0,2)*v_3d[2]+P(0,3);
		pt3d[1] = P(1,0)*v_3d[0]+P(1,1)*v_3d[1]+P(1,2)*v_3d[2]+P(1,3);
		pt3d[2] = P(2,0)*v_3d[0]+P(2,1)*v_3d[1]+P(2,2)*v_3d[2]+P(2,3);
		pt3d[0] /= pt3d[2];
		pt3d[1] /= pt3d[2];
		pt3d[2] = 1.0;

		Wml::Matrix3d &K = Ks[i];

		RMSE += Pow2(K(0, 0)*pt3d[0] + K(0, 2) - points_2d[i].X()) + Pow2(K(1, 1) * pt3d[1] + K(1, 2) - points_2d[i].Y());
	}

	RMSE = sqrt(RMSE/iCount);

	return RMSE;
}


void Calibrate(double &x, double &y,  Wml::Matrix3d &K)
{
	double skew = 0;
	double calibx, caliby;

	caliby = (y - K(1, 2)) / K(1, 1);
	calibx = (x - K(0, 2)) / K(0, 0) - caliby * K(0, 1) / K(0, 0);

	x = calibx;
	y = caliby;
}

double Estimate3D(std::vector<Wml::Vector2d> &points_2d, 
				  std::vector<Wml::Matrix4d> &cameras, 
				  std::vector<Wml::Matrix3d> &Ks,
				  Wml::Vector3d &initx)
{
	ZOptimizerLM<double> optimizer;
	
	ZReprojective_LinearExpress express;
	ZReprojective_NonLinearExpress express2;
	Wml::GVectord x(3);
	double MinValue=0;

	int iPtCount = points_2d.size();

	express.PList.resize(iPtCount);
	express.m_points.resize(iPtCount);
	express.wList.resize(iPtCount);

	express2.PList.resize(iPtCount);
	express2.m_points.resize(iPtCount);
	express2.wList.resize(iPtCount);

	for(int i = 0; i < iPtCount; ++ i)
	{
		double x = points_2d[i].X();
		double y = points_2d[i].Y();

		express.PList[i] = cameras[i];

		Calibrate(x, y, Ks[i]);

		express.m_points[i].X() = x;
		express.m_points[i].Y() = y;

		express.wList[i] = (Ks[i][0][0] + Ks[i][1][1]) / 2.0;

		express2.PList[i] = express.PList[i];
		express2.m_points[i] = express.m_points[i];
		express2.wList[i] = express.wList[i];
	}

	x[0] = initx[0];
	x[1] = initx[1];
	x[2] = initx[2];

	optimizer.Optimize(express,x,MinValue);
	optimizer.Optimize(express2,x,MinValue);

	initx[0] = x[0];
	initx[1] = x[1];
	initx[2] = x[2];

	double RMSE = Track_RMSE(points_2d, cameras, Ks, initx);
	
	return RMSE;
}

double Estimate3D(std::vector<Wml::Vector2d> &points_2d,
				  std::vector<Wml::Matrix4d> &cameras,
				  std::vector<Wml::Matrix3d> &Ks,
				  Wml::Vector3d &initx,
				  std::vector<int>& keylist)
{
	std::vector<Wml::Vector2d> key_points_2d;
	std::vector<Wml::Matrix4d> key_cameras;
	std::vector<Wml::Matrix3d> key_Ks;

	for (int i = 0; i < keylist.size(); ++ i)
	{
		key_points_2d.push_back(points_2d[keylist[i]]);
		key_cameras.push_back(cameras[keylist[i]]);
		key_Ks.push_back(Ks[keylist[i]]);
	}

	return Estimate3D(key_points_2d, key_cameras, key_Ks, initx);
}