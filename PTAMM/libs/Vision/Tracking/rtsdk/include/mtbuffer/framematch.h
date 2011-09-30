#pragma once

#include <map>

//////////////////////////////////////////////////////////////////////////
/// \ingroup Buffer
/// \brief Temporary data structure for detection, matching and solving.
///
/// The 2D image coordinate and 3D structure pair.
//////////////////////////////////////////////////////////////////////////

/// keyindex for marker index, pointindex for point index.
class matchkey
{
public:
	int keyindex;
	int pointindex;

	matchkey(int key, int point)
	{
		keyindex = key;
		pointindex = point;
	}
	matchkey()
	{
		keyindex = -1;
		pointindex = -1;
	}

	bool operator == (const matchkey& obj)
	{
		return ((keyindex == obj.keyindex) && (pointindex == obj.pointindex));
	}
};

class frame_match
{
public:
	frame_match(){}
	frame_match(float ox2, float oy2, float fx2, float fy2, float fx3, float fy3, float fz3, matchkey match, int flag = 0):
	    orix2(ox2), oriy2(oy2), x2(fx2),y2(fy2),x3(fx3),y3(fy3),z3(fz3),
		_match(match),_flag(flag){}

	frame_match(const frame_match& object)
	{
		orix2 = object.orix2;
		oriy2 = object.oriy2;
		x2 = object.x2;
		y2 = object.y2;
		x3 = object.x3;
		y3 = object.y3;
		z3 = object.z3;

		_match = object._match;
		_flag = object._flag;

		_featureValue = object._featureValue;

		_matchValue = object._matchValue;
	}
	float orix2;
	float oriy2;
	float x2;
	float y2;
	float x3;
	float y3;
	float z3;

	// \brief The matched keypoint index.
	matchkey _match;

	// \brief 0 for SIFT feature, 1 for KLT feature.
	int _flag;

	// \brief feature measurement: larger value more stable.
	float _featureValue;

	// \brief match reliability:0-1, larger value less reliable.
	float _matchValue;
};
