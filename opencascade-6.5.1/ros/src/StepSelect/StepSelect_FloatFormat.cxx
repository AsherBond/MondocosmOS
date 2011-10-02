#include <StepSelect_FloatFormat.ixx>
#include <Interface_FloatWriter.hxx>
#include <stdio.h>



    StepSelect_FloatFormat::StepSelect_FloatFormat ()
    : thezerosup (Standard_True) , themainform ("%E") ,
      theformrange ("%f") , therangemin (0.1) , therangemax (1000.)
      {  }

    void  StepSelect_FloatFormat::SetDefault (const Standard_Integer digits)
{
  themainform.Clear();
  theformrange.Clear();
  if (digits <= 0) {
    themainform.AssignCat  ("%E");
    theformrange.AssignCat ("%f");
  } else {
    char format[20];
    char pourcent = '%'; char point = '.';
    sprintf(format,  "%c%d%c%dE",pourcent,digits+2,point,digits);
    themainform.AssignCat  (format);
    sprintf(format,  "%c%d%c%df",pourcent,digits+2,point,digits);
    theformrange.AssignCat (format);
  }
  therangemin = 0.1; therangemax = 1000.;
  thezerosup = Standard_True;
}

    void  StepSelect_FloatFormat::SetZeroSuppress (const Standard_Boolean mode)
      {  thezerosup = mode;  }

    void  StepSelect_FloatFormat::SetFormat (const Standard_CString format)
      {  themainform.Clear();  themainform.AssignCat(format);  }


    void  StepSelect_FloatFormat::SetFormatForRange
  (const Standard_CString form, const Standard_Real R1, const Standard_Real R2)
{
  theformrange.Clear();  theformrange.AssignCat(form);
  therangemin = R1;  therangemax = R2;
}

    void  StepSelect_FloatFormat::Format
  (Standard_Boolean& zerosup,  TCollection_AsciiString& mainform,
   Standard_Boolean& hasrange, TCollection_AsciiString& formrange,
   Standard_Real& rangemin,    Standard_Real& rangemax) const
{
  zerosup   = thezerosup;
  mainform  = themainform;
  hasrange  = (theformrange.Length() > 0);
  formrange = theformrange;
  rangemin  = therangemin;
  rangemax  = therangemax;
}


    void  StepSelect_FloatFormat::Perform
  (IFSelect_ContextWrite& ctx,
   StepData_StepWriter& writer) const
{
  writer.FloatWriter().SetFormat (themainform.ToCString());
  writer.FloatWriter().SetZeroSuppress (thezerosup);
  if (theformrange.Length() > 0) writer.FloatWriter().SetFormatForRange
    (theformrange.ToCString(), therangemin, therangemax);
}

    TCollection_AsciiString  StepSelect_FloatFormat::Label () const
{
  TCollection_AsciiString lab("Float Format ");
  if (thezerosup) lab.AssignCat(" ZeroSuppress");
  lab.AssignCat (themainform);
  if (theformrange.Length() > 0) {
    char mess[30];
    sprintf(mess,", in range %f %f %s",
	    therangemin,therangemax,theformrange.ToCString());
    lab.AssignCat(mess);
  }
  return lab;
}
