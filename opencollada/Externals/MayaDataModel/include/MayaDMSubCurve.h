/*
    Copyright (c) 2008-2009 NetAllied Systems GmbH

    This file is part of MayaDataModel.

    Licensed under the MIT Open Source License,
    for details please see LICENSE file or the website
    http://www.opensource.org/licenses/mit-license.php
*/
#ifndef __MayaDM_SUBCURVE_H__
#define __MayaDM_SUBCURVE_H__
#include "MayaDMTypes.h"
#include "MayaDMConnectables.h"
#include "MayaDMCurveRange.h"
namespace MayaDM
{
class SubCurve : public CurveRange
{
public:
public:

	SubCurve():CurveRange(){}
	SubCurve(FILE* file,const std::string& name,const std::string& parent="",bool shared=false,bool create=true)
		:CurveRange(file, name, parent, "subCurve", shared, create){}
	virtual ~SubCurve(){}

	void setMinValue(double min)
	{
		if(min == 0.0) return;
		fprintf(mFile,"\tsetAttr \".min\" %f;\n", min);
	}
	void setMaxValue(double max)
	{
		if(max == -1.0) return;
		fprintf(mFile,"\tsetAttr \".max\" %f;\n", max);
	}
	void setRelative(bool r)
	{
		if(r == false) return;
		fprintf(mFile,"\tsetAttr \".r\" %i;\n", r);
	}
	void getInputCurve()const
	{
		fprintf(mFile,"\"%s.ic\"",mName.c_str());
	}
	void getMinValue()const
	{
		fprintf(mFile,"\"%s.min\"",mName.c_str());
	}
	void getMaxValue()const
	{
		fprintf(mFile,"\"%s.max\"",mName.c_str());
	}
	void getRelative()const
	{
		fprintf(mFile,"\"%s.r\"",mName.c_str());
	}
	void getOutputCurve()const
	{
		fprintf(mFile,"\"%s.oc\"",mName.c_str());
	}
protected:
	SubCurve(FILE* file,const std::string& name,const std::string& parent,const std::string& nodeType,bool shared=false,bool create=true)
		:CurveRange(file, name, parent, nodeType, shared, create) {}

};
}//namespace MayaDM
#endif//__MayaDM_SUBCURVE_H__
