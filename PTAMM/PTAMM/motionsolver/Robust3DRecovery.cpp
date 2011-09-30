#include "Robust3DRecovery.h"
#include "zoptimizerlm.h"
#include <iostream>
#include <algorithm>
#include <set>

#include "Estimate3DStructure.h"

CRobust3DRecovery::CRobust3DRecovery(void)
{
}

CRobust3DRecovery::~CRobust3DRecovery(void)
{
}

std::vector<int> CRobust3DRecovery::generate(std::vector<Wml::Vector2d> &points_2d, std::vector<Wml::Matrix4d> &cameras,
											 std::vector<Wml::Matrix3d> &Ks,
											 Wml::Vector3d& point_3d, int min_sample, double threshold, int iteration)
{
	// the set of good points.
	std::vector<int> best_points;

	// The new estimated x.
	Wml::Vector3d best_3d = point_3d;

	// Sum of fitting error of all good points.
	double best_error = 1e100;

	//////////////////////////////////////////////////////////////////////////
	/// check the size.
	int point_num = points_2d.size();

	if (point_num < 2)
	{
		std::cout<<"too small point size. \n";
		return best_points;
	}

	//////////////////////////////////////////////////////////////////////////

	// Random seeds for the random sampling.
	srand( (unsigned)time( NULL ) );

	for (int ki = 0 ; ki < iteration; ++ki)
	{
		// Build random samples
		std::set<int> sampleindex;
		do
		{			
			// Random index.
			sampleindex.insert(rand()%point_num);

		}while(sampleindex.size() < min_sample);
		
		// collect samples;
		std::vector<Wml::Vector2d> keypoints;
		std::vector<Wml::Matrix4d> keycameras;
		std::vector<Wml::Matrix3d> keyKs;

		for (std::set<int>::iterator it = sampleindex.begin(); it != sampleindex.end(); ++ it)
		{
			keypoints.push_back(points_2d[*it]);
			keycameras.push_back(cameras[*it]);
			keyKs.push_back(Ks[*it]);
		}

		//estimate it!
		Wml::Vector3d temp_params = best_3d;
		double error = Estimate3D(keypoints, keycameras, keyKs, temp_params);
		
		//////////////////////////////////////////////////////////////////////////

		//check the points for good ones!
		double total_error = 0.0;

		// Good points list.
		std::vector<int> good_points;
		for(int i = 0; i < point_num; ++ i)
		{
			double tmp_error = Track_RMSE(points_2d[i], cameras[i], Ks[i], temp_params);

			//printf("%f\t",fitError/threshold*3.0);
			if (tmp_error < threshold)
			{
				good_points.push_back(i);
				total_error += tmp_error;
			}			
		}

		total_error /= good_points.size();

		// if the number of good point is larger.
		if(good_points.size() > best_points.size())
		{
			// If current fitted model matches the points better than the previous.
			best_points = good_points;
			best_error = total_error;
			best_3d = temp_params;

			// If the fitted model matches most of the points.
			if(best_points.size() >= point_num * 0.8)
				break;
		}
		// if the error is smaller.
		else if(good_points.size() == best_points.size() && total_error < best_error)
		{
			// If current fitted model matches the points as the previous, compare the fitting error.
			best_points = good_points;
			best_error = total_error;
			best_3d = temp_params;
		}
	}

	printf("total points:%d, good points:%d\n", point_num, best_points.size());

	// restimate with all good points.
	if(best_points.size() > min_sample)
	{
		// collect samples;
		Estimate3D(points_2d, cameras, Ks, best_3d);

		//check the points for good ones!
		double total_error = 0.0;

		// Good points list.
		for(int i = 0; i < point_num; ++ i)
		{
			double tmp_error = Track_RMSE(points_2d[i], cameras[i], Ks[i], best_3d);
			total_error += tmp_error;
		}

		total_error /= point_num;

		std::cout<<"error = "<<total_error<<std::endl;
	}

	point_3d = best_3d;
	return best_points;
}