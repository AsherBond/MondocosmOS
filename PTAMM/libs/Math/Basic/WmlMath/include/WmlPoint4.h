// Magic Software, Inc.
// http://www.magic-software.com
// http://www.wild-magic.com
// Copyright (c) 2004.  All Rights Reserved
//
// The Wild Magic Library (WML) source code is supplied under the terms of
// the license agreement http://www.magic-software.com/License/WildMagic.pdf
// and may not be copied or disclosed except in accordance with the terms of
// that agreement.

#ifndef WMLPOINT4_H
#define WMLPOINT4_H

#include "WmlPoint.h"

namespace Wml
{

template <class Real>
class Point4 : public Point<4,Real>
{
public:
    // construction
    Point4 ();
    Point4 (Real fX, Real fY, Real fZ, Real fW);
    Point4 (const Point4& rkP);
    Point4 (const Point<4,Real>& rkP);

    // member access
    Real X () const;
    Real& X ();
    Real Y () const;
    Real& Y ();
    Real Z () const;
    Real& Z ();
    Real W () const;
    Real& W ();

    // special point
    WML_ITEM static const Point4 ZERO;

protected:
    using Point<4,Real>::m_afTuple;
};

#include "WmlPoint4.inl"

typedef Point4<float> Point4f;
typedef Point4<double> Point4d;

}

#endif
