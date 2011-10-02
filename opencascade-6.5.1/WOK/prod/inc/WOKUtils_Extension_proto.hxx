// File:	WOKUtils_Extension_Proto.hxx
// Created:	Fri Jan 31 20:31:18 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#ifndef _WOKUtils_Extension_HeaderFile
#define _WOKUtils_Extension_HeaderFile


#ifdef WNT

#include <WOKNT_Extension.hxx>

typedef  WOKNT_Extension WOKUtils_Extension;


#define WOKUtils_CFile WOKNT_CFile
#define WOKUtils_HFile WOKNT_HFile
#define WOKUtils_CDLFile WOKNT_CDLFile
#define WOKUtils_ODLFile WOKNT_ODLFile
#define WOKUtils_IDLFile WOKNT_IDLFile
#define WOKUtils_CXXFile WOKNT_CXXFile
#define WOKUtils_HXXFile WOKNT_HXXFile
#define WOKUtils_IXXFile WOKNT_IXXFile
#define WOKUtils_JXXFile WOKNT_JXXFile
#define WOKUtils_LXXFile WOKNT_LXXFile
#define WOKUtils_GXXFile WOKNT_GXXFile
#define WOKUtils_INCFile WOKNT_INCFile
#define WOKUtils_PXXFile WOKNT_PXXFile
#define WOKUtils_F77File WOKNT_F77File
#define WOKUtils_CSHFile WOKNT_CSHFile
#define WOKUtils_DBFile WOKNT_DBFile
#define WOKUtils_FDDBFile WOKNT_FDDBFile
#define WOKUtils_DDLFile WOKNT_DDLFile
#define WOKUtils_HO2File WOKNT_HO2File
#define WOKUtils_LibSchemaFile WOKNT_LibSchemaFile
#define WOKUtils_AppSchemaFile WOKNT_AppSchemaFile
#define WOKUtils_LexFile WOKNT_LexFile
#define WOKUtils_YaccFile WOKNT_YaccFile
#define WOKUtils_PSWFile WOKNT_PSWFile
#define WOKUtils_LWSFile WOKNT_LWSFile
#define WOKUtils_TemplateFile WOKNT_TemplateFile
#define WOKUtils_ObjectFile WOKNT_ObjectFile
#define WOKUtils_MFile WOKNT_MFile
#define WOKUtils_CompressedFile WOKNT_CompressedFile
#define WOKUtils_ArchiveFile WOKNT_ArchiveFile
#define WOKUtils_DSOFile WOKNT_DSOFile
#define WOKUtils_DATFile WOKNT_DATFile
#define WOKUtils_LispFile WOKNT_LispFile
#define WOKUtils_IconFile WOKNT_IconFile
#define WOKUtils_TextFile WOKNT_TextFile
#define WOKUtils_TarFile WOKNT_TarFile
#define WOKUtils_LIBFile WOKNT_LIBFile
#define WOKUtils_DEFile WOKNT_DEFile
#define WOKUtils_RCFile WOKNT_RCFile
#define WOKUtils_RESFile WOKNT_RESFile
#define WOKUtils_IMPFile WOKNT_IMPFile
#define WOKUtils_EXPFile WOKNT_EXPFile
#define WOKUtils_DLLFile WOKNT_DLLFile
#define WOKUtils_PDBFile WOKNT_PDBFile
#define WOKUtils_EXEFile WOKNT_EXEFile

#define WOKUtils_UnknownFile WOKNT_UnknownFile
#define WOKUtils_NoExtFile WOKNT_NoExtFile

#else

#include <WOKUnix_Extension.hxx>

typedef  WOKUnix_Extension WOKUtils_Extension;

#define WOKUtils_CFile WOKUnix_CFile
#define WOKUtils_HFile WOKUnix_HFile
#define WOKUtils_CDLFile WOKUnix_CDLFile
#define WOKUtils_ODLFile WOKUnix_ODLFile
#define WOKUtils_IDLFile WOKUnix_IDLFile
#define WOKUtils_CXXFile WOKUnix_CXXFile
#define WOKUtils_HXXFile WOKUnix_HXXFile
#define WOKUtils_IXXFile WOKUnix_IXXFile
#define WOKUtils_JXXFile WOKUnix_JXXFile
#define WOKUtils_LXXFile WOKUnix_LXXFile
#define WOKUtils_GXXFile WOKUnix_GXXFile
#define WOKUtils_INCFile WOKUnix_INCFile
#define WOKUtils_PXXFile WOKUnix_PXXFile
#define WOKUtils_F77File WOKUnix_F77File
#define WOKUtils_CSHFile WOKUnix_CSHFile
#define WOKUtils_DBFile WOKUnix_DBFile
#define WOKUtils_FDDBFile WOKUnix_FDDBFile
#define WOKUtils_DDLFile WOKUnix_DDLFile
#define WOKUtils_HO2File WOKUnix_HO2File
#define WOKUtils_LibSchemaFile WOKUnix_LibSchemaFile
#define WOKUtils_AppSchemaFile WOKUnix_AppSchemaFile
#define WOKUtils_LexFile WOKUnix_LexFile
#define WOKUtils_YaccFile WOKUnix_YaccFile
#define WOKUtils_PSWFile WOKUnix_PSWFile
#define WOKUtils_LWSFile WOKUnix_LWSFile
#define WOKUtils_TemplateFile WOKUnix_TemplateFile
#define WOKUtils_ObjectFile WOKUnix_ObjectFile
#define WOKUtils_MFile WOKUnix_MFile
#define WOKUtils_CompressedFile WOKUnix_CompressedFile
#define WOKUtils_ArchiveFile WOKUnix_ArchiveFile
#define WOKUtils_DSOFile WOKUnix_DSOFile
#define WOKUtils_DATFile WOKUnix_DATFile
#define WOKUtils_LispFile WOKUnix_LispFile
#define WOKUtils_IconFile WOKUnix_IconFile
#define WOKUtils_TextFile WOKUnix_TextFile
#define WOKUtils_TarFile WOKUnix_TarFile
#define WOKUtils_LIBFile WOKUnix_LIBFile
#define WOKUtils_DEFile WOKUnix_DEFile
#define WOKUtils_RCFile WOKUnix_RCFile
#define WOKUtils_RESFile WOKUnix_RESFile
#define WOKUtils_IMPFile WOKUnix_IMPFile
#define WOKUtils_EXPFile WOKUnix_EXPFile
#define WOKUtils_UnknownFile WOKUnix_UnknownFile
#define WOKUtils_NoExtFile WOKUnix_NoExtFile
#define WOKUtils_DLLFile WOKUnix_DLLFile
#define WOKUtils_PDBFile WOKUnix_PDBFile
#define WOKUtils_EXEFile WOKUnix_EXEFile

#endif

#ifndef _Standard_PrimitiveTypes_HeaderFile
#include <Standard_PrimitiveTypes.hxx>
#endif

#endif
