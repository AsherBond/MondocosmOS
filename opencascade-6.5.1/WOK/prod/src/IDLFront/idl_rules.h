enum IDL_State { IDL_NONE,IDL_INTERFACEDECL};

struct IDLGlobal {
  char       idname[256];
  char       interfacename[256];
  IDL_State  traductorstate;
};

extern "C" {
void IDL_SetIdentifier(char *); 
void IDL_InterfaceDeclaration();
void IDL_InterfaceDefinitionBegin();
void IDL_InterfaceDefinitionEnd();
}

