#pragma once

#include "artificialkeypoint.h"
//#include "naturalkeypoint.h"
//#include "siftmarker.h"
//#include "siftgpumarker.h"
//#include "harrismarker.h"
#include "mtbuffer/framematch.h"

namespace FeatureSpace
{	


	//////////////////////////////////////////////////////////////////////////
	/// \ingroup Marker
	/// \brief The keypoint base contains all the keypoints and related opreations.
	///
	/// 
	/// Homography mapping to a circle. This is used to build the frontal feature images that can be 
	/// matched with the preprocessed key points.
	//////////////////////////////////////////////////////////////////////////
	class DLL_EXPORT MarkerKeypointBase
	{
	public:
		//////////////////////////////////////////////////////////////////////////
		/// \brief All the keypoints.
		//////////////////////////////////////////////////////////////////////////
		std::vector<ArtificialKeypoint> m_vArtificialKP;
	//	std::vector<NaturalKeypoint> m_vNaturalKP;
		//std::vector<SIFTMarker> m_vSIFTMarker;
		//std::vector<SIFTGPUMarker> m_vSIFTGpuMarker;
		//std::vector<HarrisMarker> m_vHarrisMarker;

		//////////////////////////////////////////////////////////////////////////
		/// \brief The data file which contains all the keypoint information.
		//////////////////////////////////////////////////////////////////////////
		std::string m_strDataFile;

		//////////////////////////////////////////////////////////////////////////
		/// \brief Whether a data file is loaded.
		//////////////////////////////////////////////////////////////////////////
		bool bfile;

		/// \brief Load the data file.
		bool Load(std::string _strDataFile);
	protected:

		MarkerKeypointBase(const MarkerKeypointBase &);


		MarkerKeypointBase& operator=(const MarkerKeypointBase &);

	public:
		/// \brief Constructor.
		MarkerKeypointBase(bool = false);

		/// \brief Destructor.
		~MarkerKeypointBase();

		/// \brief Set the data file.
		bool SetFile(std::string _strDataFile);

		/// \brief Save the data file.
		bool Save(std::string _strDataFile = "");

		/// \brief Add a rectangle feature, a keypoint.
		bool Add(ArtificialFeature &f);

		/// \brief Add a sift marker, a keypoint.
		//bool Add(SIFTFeature &f);

		/// \brief Add a sift gpu marker.
		//bool Add(std::vector<SiftGPU::SiftKeypoint> &keys, std::vector<float> &descriptors);


		/// \brief Add a sift harris marker.
		//bool Add(HarrisFeature &f);

		/// \brief Find a artificial keypoint.
		matchkey Find(const ArtificialKeypoint &kp);

		void Rotate(int marker, int direction);

		/// \biref For sift marker.
		//std::vector<matchkey> Find(SIFT::CKeypoint* kp);

		/// \brief For siftgpu marker.
		int Find(float* kp);

		/// \brief For harris marker.
		std::vector<matchkey> Find(const std::vector<float> &kp, std::vector<int> markers);

		/// \brief Remove a keypoint.
		void RemoveArtificial(int i);
		void RemoveNatural(int i);
	};
}
DLL_EXP_OBJ FeatureSpace::MarkerKeypointBase g_markerkeybase;