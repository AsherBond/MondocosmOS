#include <IGESGraph_Protocol.ixx>

#include <IGESGraph_LineFontDefPattern.hxx>
#include <IGESGraph_Color.hxx>
#include <IGESGraph_LineFontPredefined.hxx>
#include <IGESGraph_DefinitionLevel.hxx>
#include <IGESGraph_LineFontDefTemplate.hxx>
#include <IGESGraph_DrawingSize.hxx>
#include <IGESGraph_NominalSize.hxx>
#include <IGESGraph_DrawingUnits.hxx>
#include <IGESGraph_Pick.hxx>
#include <IGESGraph_TextDisplayTemplate.hxx>
#include <IGESGraph_HighLight.hxx>
#include <IGESGraph_TextFontDef.hxx>
#include <IGESGraph_IntercharacterSpacing.hxx>
#include <IGESGraph_UniformRectGrid.hxx>

#include <IGESBasic.hxx>
#include <IGESBasic_Protocol.hxx>

static int deja = 0;
static Handle(Standard_Type) atype01,atype02,atype03,atype04,atype05,atype06,
  atype07,atype08,atype09,atype10,atype11,atype12,atype13,atype14;

    IGESGraph_Protocol::IGESGraph_Protocol ()
{
  if (deja) return;  deja = 1;
  atype01 = STANDARD_TYPE(IGESGraph_Color);
  atype02 = STANDARD_TYPE(IGESGraph_DefinitionLevel);
  atype03 = STANDARD_TYPE(IGESGraph_DrawingSize);
  atype04 = STANDARD_TYPE(IGESGraph_DrawingUnits);
  atype05 = STANDARD_TYPE(IGESGraph_HighLight);
  atype06 = STANDARD_TYPE(IGESGraph_IntercharacterSpacing);
  atype07 = STANDARD_TYPE(IGESGraph_LineFontDefPattern);
  atype08 = STANDARD_TYPE(IGESGraph_LineFontPredefined);
  atype09 = STANDARD_TYPE(IGESGraph_LineFontDefTemplate);
  atype10 = STANDARD_TYPE(IGESGraph_NominalSize);
  atype11 = STANDARD_TYPE(IGESGraph_Pick);
  atype12 = STANDARD_TYPE(IGESGraph_TextDisplayTemplate);
  atype13 = STANDARD_TYPE(IGESGraph_TextFontDef);
  atype14 = STANDARD_TYPE(IGESGraph_UniformRectGrid);
}

    Standard_Integer IGESGraph_Protocol::NbResources () const
      {  return 1;  }

    Handle(Interface_Protocol) IGESGraph_Protocol::Resource
  (const Standard_Integer num) const
{
  Handle(Interface_Protocol) res = IGESBasic::Protocol();;
  return res;
}

    Standard_Integer IGESGraph_Protocol::TypeNumber
  (const Handle(Standard_Type)& atype) const
{
  if      (atype == atype01) return  1;
  else if (atype == atype02) return  2;
  else if (atype == atype03) return  3;
  else if (atype == atype04) return  4;
  else if (atype == atype05) return  5;
  else if (atype == atype06) return  6;
  else if (atype == atype07) return  7;
  else if (atype == atype08) return  8;
  else if (atype == atype09) return  9;
  else if (atype == atype10) return 10;
  else if (atype == atype11) return 11;
  else if (atype == atype12) return 12;
  else if (atype == atype13) return 13;
  else if (atype == atype14) return 14;
  return 0;
}
