#ifndef _WOKDeliv_ParseDelivery_HeaderFile

#include <string.h>
extern int TheToken;
extern int DELIVERYlineno;
extern char* TheText;
extern int TheType;
extern int TheAttrib;
extern int DELIVERYlex   ();
extern int DELIVERYerror ();
int Traite_PutPath();
int Traite_PutInclude();
int Traite_PutLib();
int Traite_GetUnit(char* s);
int Traite_GetType(char* s);
int Traite_GetFile(char* s);
int Traite_Name(char* s);
int Traite_Requires(char* s);
int Traite_Ifdef(char* s);
int Traite_Endif();
int ClasseElt_DeliverFormatAll(int tokunit, char* s);
int ClasseElt_DeliverFormatBase(int tokunit, char* s);
int ClasseElt_DeliverFormat(int tokattr);
int ClasseElt_EndDeliverFormat();


#define _WOKDeliv_ParseDelivery_HeaderFile
#endif
