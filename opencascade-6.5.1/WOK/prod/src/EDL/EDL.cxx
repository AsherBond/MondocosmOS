#include <EDL.ixx>
#include <TCollection_AsciiString.hxx>

#include <stdio.h>

#ifndef WNT
extern int EDLlineno;
#else
extern "C" int EDLlineno;
#endif  // WNT
extern TCollection_AsciiString EDL_CurrentFile;


static void (*EDL_ErrorMsgHandler) (Standard_CString aMsg) = NULL;

Standard_EXPORT void EDL_SetErrorMsgHandler(void (*aMsgHandler) (Standard_CString aMsg))
{
   EDL_ErrorMsgHandler = aMsgHandler;
   return;
}

void EDL::PrintError(const EDL_Error anError, const Standard_CString anArg)
{
const char *format;
const char *errortext = "";

  if (EDLlineno >= 0) {
    format = "%s : line %d : %s%s\n";
  }
  else {
    format = "call from C++ : %s%s\n";
  }

  switch (anError) {
  case EDL_NORMAL: errortext = "Done : ";
		   break;
  case EDL_SYNTAXERROR: errortext = "Syntax error";
		   break;
  case EDL_VARNOTFOUND: errortext = "Variable not found : ";
		   break;
  case EDL_TEMPMULTIPLEDEFINED: errortext = "Template already defined : ";
		   break;
  case EDL_TEMPLATENOTDEFINED: errortext = "Template not defined : ";
		   break;
  case EDL_LIBRARYNOTFOUND: errortext = "Library not found : ";
		   break;
  case EDL_LIBNOTOPEN: errortext = "Library not open : ";
		   break;
  case EDL_FUNCTIONNOTFOUND: errortext = "Function not found : ";
		   break;
  case EDL_FILEOPENED: errortext = "File opened : ";
		   break;
  case EDL_FILENOTOPENED: errortext = "File not opened : ";
		   break;
  case EDL_TOOMANYINCLUDELEVEL: errortext = "Too many include levels : ";
		   break;
  case EDL_FILENOTFOUND:  errortext = "File not found : ";
                   break;
  }

  if(!EDL_ErrorMsgHandler) {
	if (EDLlineno >= 0) {
	    printf(format,EDL_CurrentFile.ToCString(),EDLlineno,errortext,anArg);
	}
	else {
	    printf(format,errortext,anArg);
	}
  } else {
	char TheMsg[1024];
	if (EDLlineno >= 0) {
	    sprintf(TheMsg,format,EDL_CurrentFile.ToCString(),EDLlineno,errortext,anArg);
	}
	else {
	    sprintf(TheMsg, format,errortext,anArg);
	}
	(*EDL_ErrorMsgHandler) (TheMsg);
  }

}
