// ----------------------------------------------------------------
// Historique :
// ----------------------------------------------------------------
// 12/04/98 : CRD : Suppression des cout.
// 22/10/92 : GDE : Creation
// ----------------------------------------------------------------


#include <Materials.hxx>
#include <Materials_MaterialsDictionary.hxx>
#include <Materials_MaterialDefinition.hxx>
#include <Materials_Material.hxx>
#include <Standard_PCharacter.hxx>

static Handle(Materials_MaterialsDictionary) materialsdictionary;
static Handle(Materials_MaterialDefinition) materialdefinition;

static Standard_PCharacter materialfile;
static Standard_PCharacter materialsfile;

void  DictionaryOfDefinitions(Handle(Materials_MaterialDefinition)&);

//=======================================================================
//function : MaterialFile
//purpose  : 
//=======================================================================

void Materials::MaterialFile(const Standard_CString afile)
{
  Standard_Integer length = strlen(afile);
  materialfile = new Standard_Character[length+1];
  strcpy(materialfile,afile);
  materialfile[length] = 0;
}

//=======================================================================
//function : MaterialsFile
//purpose  : 
//=======================================================================

void Materials::MaterialsFile(const Standard_CString afile)
{
  Standard_Integer length = strlen(afile);
  materialsfile = new Standard_Character[length+1];
  strcpy(materialsfile,afile);
  materialsfile[length] = 0;
}

//=======================================================================
//function : MaterialsFile
//purpose  : 
//=======================================================================

Standard_CString Materials::MaterialsFile()
{
  return materialsfile;
}

//=======================================================================
//function : DictionaryOfMaterials
//purpose  : 
//=======================================================================

Handle(Materials_MaterialsDictionary) Materials::DictionaryOfMaterials()
{
  if(materialsdictionary.IsNull())
    {
      materialsdictionary  = new Materials_MaterialsDictionary();
    }
  else if(!materialsdictionary->UpToDate())
    {
      materialsdictionary  = new Materials_MaterialsDictionary();
    }
  return materialsdictionary;
}


Handle(Materials_Material) Materials::Material(const Standard_CString amaterial)
{
  return (Materials::DictionaryOfMaterials())->Material(amaterial);
}

Standard_Boolean Materials::ExistMaterial(const Standard_CString aName)
{
  return (Materials::DictionaryOfMaterials())->ExistMaterial(aName);
}

void  DictionaryOfDefinitions (Handle(Materials_MaterialDefinition)& adictionary)
{
  if(materialdefinition.IsNull())
    {
      materialdefinition = new Materials_MaterialDefinition();
      materialdefinition->Creates(materialfile);
    }
  else if(!materialdefinition->UpToDate())
    {
      materialdefinition->Creates(materialfile);
    }
  adictionary = materialdefinition;
}

Standard_Integer Materials::NumberOfMaterials()
{
  return (Materials::DictionaryOfMaterials())->NumberOfMaterials();
}

Handle(Materials_Material) Materials::Material(const Standard_Integer anindex)
{
  return (Materials::DictionaryOfMaterials())->Material(anindex);
}

