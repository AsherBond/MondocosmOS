// Magic Software, Inc.
// http://www.magic-software.com
// http://www.wild-magic.com
// Copyright (c) 2004.  All Rights Reserved
//
// The Wild Magic Library (WML) source code is supplied under the terms of
// the license agreement http://www.magic-software.com/License/WildMagic.pdf
// and may not be copied or disclosed except in accordance with the terms of
// that agreement.

#ifndef WMLPOINT2_H
#define WMLPOINT2_H

#include "WmlPoint.h"

namespace Wml
{

template <class Real>
class Point2 : public Point<2,Real>
{
public:
    // construction
    Point2 ();
    Point2 (Real fX, Real fY);
    Point2 (const Point2& rkP);
    Point2 (const Point<2,Real>& rkP);

    // member access
    Real X () const;
    Real& X ();
    Real Y () const;
    Real& Y ();

    // special point
    WML_ITEM static const Point2 ZERO;

protected:
    using Point<2,Real>::m_afTuple;
};

#include "WmlPoint2.inl"

typedef Point2<float> Point2f;
typedef Point2<double> Point2d;

}

#endif
