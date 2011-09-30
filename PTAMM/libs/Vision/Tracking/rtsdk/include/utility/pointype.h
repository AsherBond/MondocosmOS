#pragma once

namespace FeatureSpace
{
//////////////////////////////////////////////////////////////////////////
/// \ingroup Marker
/// \brief 2D coordinate.
//////////////////////////////////////////////////////////////////////////
class d2Point
{
public:
	// image coordinate
	d2Point(float x, float y)
	{
		x2 = x;
		y2 = y;
	}

	d2Point()
	{
		x2 = 0.0f;
		y2 = 0.0f;
	}
	float x2;
	float y2;
};

//////////////////////////////////////////////////////////////////////////
/// \ingroup Marker
/// \brief 3D coordinate..
//////////////////////////////////////////////////////////////////////////
class d3Point
{
public:
	// space coordinate
	d3Point()
	{
		x3 = 0.0f;
		y3 = 0.0f;
		z3 = 0.0f;
	}
	d3Point(float x, float y, float z)
	{
		x3 = x;
		y3 = y;
		z3 = z;
	}
	float x3;
	float y3;
	float z3;
};

//////////////////////////////////////////////////////////////////////////
/// \ingroup Marker
/// \brief Pair of 2D and 3D coordinates.
//////////////////////////////////////////////////////////////////////////
class d5Point:public d2Point, public d3Point
{
	public:
		d5Point(){}
		d5Point(float x22, float y22, float x33, float y33, float z33):d2Point(x22, y22),d3Point(x33, y33, z33)
		{

		}
};
}