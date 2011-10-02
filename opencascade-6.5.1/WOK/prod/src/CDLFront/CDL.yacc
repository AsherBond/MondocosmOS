/*==========================================================================*/
/*== CDL Translater      Version 1.2
/*==
/*== SCCS	Date: 17/11/93
/*== 		Information:  @(#)cdl.yacc	1.2 
/*==========================================================================*/

/*=-=-=-= The needed includes and variabels  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
%{
/* all parser must define this variable */

#define yyv CDLv

#define MAX_CHAR     256              /* The limit of a identifier.  */
#define MAX_STRING   (MAX_CHAR * 10)  /* The limit of a string.      */
#define MAX_COMMENT  (MAX_CHAR * 300) /* The limit of comment line   */
#define ENDOFCOMMENT "\n%\n"          /* The marque of end of coment */

#define CDL_CPP      1
#define CDL_FOR      2
#define CDL_C        3
#define CDL_OBJ      4
#define CDL_LIBRARY  5
#define CDL_EXTERNAL 6

#include <stdlib.h>
#include <cdl_rules.h>

extern void CDLerror ( char* );
extern int  CDLlex   ( void  );

%}
/*=-=-=-= End of The needed includes and variabels   =-=-=-=-=-=-=-=-=-=-=-=*/

%token  	krc
%token		cpp
%token		fortran
%token		object
%token 		library
%token		external
%token		alias
%token		any
%token          asynchronous
%token		as
%token		class
%token		client
%token 		component
%token		deferred
%token		schema
%token		end
%token		engine
%token		enumeration
%token		exception
%token		executable
%token		execfile
%token		extends
%token		fields
%token		friends
%token		CDL_from
%token		generic
%token		immutable
%token		imported
%token		in
%token		inherits 
%token		instantiates
%token		interface
%token		is 
%token		like
%token		me
%token		mutable
%token		myclass
%token		out
%token		package
%token		pointer
%token		private
%token		primitive
%token		protected
%token		raises 
%token		redefined
%token		returns
%token		statiC
%token		CDL_to
%token		uses
%token          virtual 
%token		')'
%token		'('
%token		']'
%token		'['
%token		':'
%token		';'
%token 		','
%token		'='
%token		IDENTIFIER
%token          JAVAIDENTIFIER
%token		INTEGER
%token		LITERAL
%token		REAL
%token		STRING
%token		INVALID
%union {
 char str[MAX_STRING];
}

%type  <str>    __Package_Name Package_Name Interface_Name __Interface_Name __Schema_Name __ExecFile_Name IDENTIFIER JAVAIDENTIFIER INTEGER REAL STRING LITERAL Schema_Name ExecFile_Name Empty_Str Engine_Name __Engine_Name Component_Name __Component_Name Client_Name

%start		__Cdl_Declaration_List
%%

/*==========================================================================*/
/*==== CDL Definition ======================================================*/
/*==========================================================================*/
__Cdl_Declaration_List	: Empty
			| Cdl_Declaration_List
			;
Cdl_Declaration_List	: Cdl_Declaration
			| Cdl_Declaration_List Cdl_Declaration
			;
Cdl_Declaration		: Package_Declaration
			| Interface_Declaration
			| Schema_Declaration
			| Engine_Declaration
			| Client_Declaration
			| Executable_Declaration
			| Component_Declaration
			| Separated_Declaration 
			
			/*======= The declaration error ====================*/
			| error	
			;
/*==========================================================================*/
/*==== Package definition ==================================================*/
/*==========================================================================*/
Package_Declaration     : package Package_Name { Pack_Begin($2); }
			     __Packages_Uses 
                          is 
			     Package_Definition
                          end __Package_Name ';' { Pack_End(); }
			;
__Package_Name		: Empty_Str 
			| Package_Name
			;
Package_Name		: IDENTIFIER  
			;
__Packages_Uses		: Empty
			| uses Package_List 
			;
Package_List		: Package_Name { Pack_Use($1); } 
			| Package_List ',' Package_Name   { Pack_Use($3); } 
			;
Package_Definition	: __Pack_Declaration_List
			  __Extern_Method_List
			;
/*==========================================================================*/
/*==== Interface definition  ===============================================*/
/*==========================================================================*/
Interface_Declaration	: interface Interface_Name  { Interface_Begin($2); }
			     __Interfaces_Uses 
                          is 
			     __Interface_Definitions
                          end __Interface_Name ';' 
			  dollardInterface_End
			;
__Interface_Name	: Empty_Str 
			|  IDENTIFIER
			;
Interface_Name		: IDENTIFIER
			;
__Interfaces_Uses	: Empty
			| uses InterfaceUse_List 
			;
InterfaceUse_List	: Package_Name {Interface_Use($1);}
			| InterfaceUse_List ',' Package_Name { Interface_Use($3); }
			;
__Interface_Definitions	: Empty
			| Interface_Definitions
			;
Interface_Definitions	: Interface_Definition
			| Interface_Definitions Interface_Definition
			;
Interface_Definition	: package Package_Name ';' { Interface_Package($2); }
			| class   Type_Name    ';' dollardInterface_Class
			| Interface_Method_Dec ';' 
			;

Interface_Method_Dec	: Method_Name Interface_Method 
			;
Interface_Method	: CDL_from __Class Type_Name { Method_TypeName(); }  Interface_Method_Class_Dec 
			| CDL_from package IDENTIFIER Friend_ExtMet_Header  Interface_returns_error {Interface_Method($3);}
			;

Interface_Method_Class_Dec: Friend_InstMet_Header  Interface_returns_error {Interface_Method("");}
			| Friend_ClassMet_Header Interface_returns_error {Interface_Method("");}
			| Friend_Const_Header Interface_returns_error {Interface_Method("");}
			;

Interface_returns_error:  Empty
			| returns Type_Name { CDLerror("in interface, method declaration can not have a 'returns' clause"); }
/*==========================================================================*/
/*==== Schema definition  ==================================================*/
/*==========================================================================*/
Schema_Declaration	: schema Schema_Name  { Schema_Begin($2); }
                          is 
			     __Schema_Packages
                          end __Schema_Name ';' { Schema_End(); }
			;
__Schema_Name		: Empty_Str 
			| Schema_Name
			;
Schema_Name		: IDENTIFIER 
			;
__Schema_Packages	: Empty
			| Schema_Packages 
			;
Schema_Packages		: Schema_Package  
			| Schema_Packages Schema_Package 
			;
Schema_Package		: package Package_Name ';' { Schema_Package($2); }
			| class Type_Name ';'      { Schema_Class(); }
			;
/*==========================================================================*/
/*==== Engine definition  ==================================================*/
/*==========================================================================*/
Engine_Declaration	: engine Engine_Name { Engine_Begin($2); } __Engine_Schema
                          is 
			     __Engine_Interfaces
                          end __Engine_Name ';' 
			  dollardEngine_End
			;

__Engine_Name		: Empty_Str
			| IDENTIFIER
			;

Engine_Name		: IDENTIFIER
			;

__Engine_Schema		: Empty
			| CDL_from __schema IDENTIFIER { Engine_Schema($3); }
			;
__schema		: Empty
			| schema
			;
__Engine_Interfaces	: Empty
			| Engine_Interfaces
			;
Engine_Interfaces	: Engine_Interface
			| Engine_Interfaces Engine_Interface
			;
Engine_Interface	: interface IDENTIFIER ';' { Engine_Interface($2); }
			;
/*==========================================================================*/
/*==== Component definition  ==================================================*/
/*==========================================================================*/
Component_Declaration	: component Component_Name { Component_Begin($2); }
                          is 
			     __Component_Interfaces
                          end __Component_Name ';' 
			  { Component_End(); }
			;

__Component_Name	: Empty_Str
			| IDENTIFIER
			;

Component_Name		: IDENTIFIER
			;

__Component_Interfaces	: Empty
			| Component_Interfaces
			;
Component_Interfaces	: Component_Interface
			| Component_Interfaces Component_Interface
			;
Component_Interface	: interface IDENTIFIER CDL_from IDENTIFIER ';' { Component_Interface($2,$4); }
			;

/*==========================================================================*/
/*==== Client definition  ==================================================*/
/*==========================================================================*/
Client_Declaration	: client Client_Name { Client_Begin($2); }
			     __Client_Uses 
                          is 
			     __Client_Definitions
                          end __Client_End ';' 
			;

Client_Name		: JAVAIDENTIFIER
                        | IDENTIFIER
			;

__Client_Uses		: Empty
			| uses ClientUse_List 
			;

ClientUse_List	        : Client_Name { Client_Use ( $1 ); }
			| ClientUse_List ',' Client_Name { Client_Use ( $3 ); }
                        ;

__Client_End		: Empty
                        | IDENTIFIER     { Client_End(); }
			| JAVAIDENTIFIER { Client_End(); }
			;

__Client_Definitions    : Empty
			| Client_Definitions
			;

Client_Definitions	: Client_Definition
			| Client_Definitions Client_Definition

Client_Definition	: interface IDENTIFIER ';' { Client_Interface($2); }
			| Method_Name Client_Method ';'
			;

Client_Method		: CDL_from __Class Type_Name { Method_TypeName(); }  Client_Method_Class_Dec 
			| CDL_from package IDENTIFIER Friend_ExtMet_Header  Client_returns_error {Client_Method($3,1);}
			| CDL_from package IDENTIFIER Friend_ExtMet_Header  Client_returns_error is asynchronous {Client_Method($3,1);}
			;

Client_Method_Class_Dec:  Friend_InstMet_Header  Client_returns_error {Client_Method("",1);}
			| Friend_InstMet_Header  Client_returns_error is asynchronous {Client_Method("",1);}
			| Friend_ClassMet_Header Client_returns_error {Client_Method("",1);}
			| Friend_ClassMet_Header Client_returns_error is asynchronous {Client_Method("",1);}
			| Friend_Const_Header    Client_returns_error {Client_Method("",-1);}
			| Friend_Const_Header    Client_returns_error is asynchronous  {Client_Method("",-1);}
			;

Client_returns_error:  Empty
			| returns Type_Name { CDLerror("in client, method declaration can not have a 'returns' clause"); }
/*==========================================================================*/
/*==== Executable definition  ==================================================*/
/*==========================================================================*/
Executable_Declaration	: executable IDENTIFIER is { Executable_Begin($2); }
				__ExecFileDeclaration
			  end __ExecutableName ';' 

__ExecFileDeclaration	: Empty
			| ExecFile_DeclarationList
			;

__ExecutableName	: Empty {  Executable_End(); }
			| IDENTIFIER { Executable_End(); }
			;

ExecFile_DeclarationList: ExecFile_Declaration	
			| ExecFile_DeclarationList ExecFile_Declaration
			;

ExecFile_Declaration	: executable ExecFile_Name { ExecFile_Begin($2); } __ExecFile_Schema
			  __ExecFile_Uses
                          is 
			     __ExecFile_Components
                          end __ExecFile_Name ';' { ExecFile_End(); }
			;

__ExecFile_Name	        : Empty_Str 
	                | ExecFile_Name
		        ;

ExecFile_Name		: IDENTIFIER 
			;

__ExecFile_Schema	: Empty
			| CDL_from __schema Schema_Name { ExecFile_Schema($3); }
			;

__ExecFile_Uses	: Empty
			| uses ExecFile_List 
			;

ExecFile_List		: ExecFile_Use
			| ExecFile_List ',' ExecFile_Use
			;
			
ExecFile_Use		:  IDENTIFIER as ExecFile_UseType {ExecFile_AddUse($1);}
			;

ExecFile_UseType	: library	{ExecFile_SetUseType(CDL_LIBRARY); }
			| external	{ExecFile_SetUseType(CDL_EXTERNAL); }
			;

__ExecFile_Components	: Empty
			| ExecFile_Components
			;

ExecFile_Components	: ExecFile_Component
			| ExecFile_Components ExecFile_Component
			;

ExecFile_Component	: IDENTIFIER __ExecFile_ComponentType ';' { ExecFile_AddComponent($1); }

__ExecFile_ComponentType : Empty    { ExecFile_SetLang(CDL_CPP); }
			   | ExecFile_ComponentType
			   ;

ExecFile_ComponentType: cpp         { ExecFile_SetLang(CDL_CPP); }
			| fortran     { ExecFile_SetLang(CDL_FOR); }
			| krc         { ExecFile_SetLang(CDL_C); }
			| object      { ExecFile_SetLang(CDL_OBJ); }
			;

/*==========================================================================*/
/*==== Type Declaration ====================================================*/
/*==========================================================================*/
__Pack_Declaration_List	: Empty
			| Pack_Declaration_List
			;
Pack_Declaration_List	: Pack_Declaration
			| Pack_Declaration_List Pack_Declaration
			;
Pack_Declaration	: Pack_Declaration_1
			| private dollardSet_Priv Pack_Declaration_1  
			;
Pack_Declaration_1	: Pack_Class_Declaration
			| Exception_Declaration
			| Enumeration_Declaration
			| Alias_Declaration
			| Imported_Declaration
			| Primitive_Declaration
			| Pointer_Declaration
			;
Separated_Declaration	: Seper_Class_Declaration
			| private dollardSet_Priv Seper_Class_Declaration 
			;
/*==========================================================================*/
/*==== Class Definition ====================================================*/
/*==========================================================================*/
Seper_Class_Declaration : Sep_Class_Declaration_1
			| deferred dollardSet_Defe Sep_Class_Declaration_1 
			;
Sep_Class_Declaration_1	: Generic_C_Declaration
			| Generic_C_Instanciation
			| NoGeneric_C_Declaration
			;
Pack_Class_Declaration	: Pac_Class_Declaration_1
			| deferred dollardSet_Defe Pac_Class_Declaration_1 
			;
Pac_Class_Declaration_1	: Inc_NoGeneric_Class 
			| Inc_Generic_Class   
			| Generic_C_Instanciation
			;
/*==== Incomplete Class ====================================================*/
Inc_NoGeneric_Class	: class dollardset_inc_state Type_Name dollardrestore_state ';' dollardInc_Class_Dec 
			;

Inc_Generic_Class	: generic class dollardset_inc_state Type_List dollardrestore_state ';'  dollardInc_GenClass_Dec
			;
/*==== Generic Class =======================================================*/
Generic_C_Declaration	: generic class Type_Name dollardGenClass_Begin
			           '(' Generic_Type_List ')'
			     __Inherits_Classes { Add_Std_Ancestors(); }
			     __Uses_Classes     { Add_Std_Uses(); }
			     __Raises_Exception
			     __Embeded_Class_List
			  is 
			     Class_Definition 
			  end __Class_Name ';' 
			  dollardGenClass_End
			;
/*==== Instanciation Class =================================================*/
Generic_C_Instanciation	: class dollardset_inc_state Type_Name dollardrestore_state
			       instantiates  dollardInstClass_Begin Type_Name dollardAdd_Gen_Class
				 '(' Type_List ')' dollardAdd_InstType  
			  ';' 
			  dollardInstClass_End
			;
/*==== No Generic Class ====================================================*/
NoGeneric_C_Declaration	: class dollardset_inc_state Type_Name dollardrestore_state dollardStdClass_Begin
			     __Inherits_Classes { Add_Std_Ancestors(); }
			     __Uses_Classes     { Add_Std_Uses(); }
			     __Raises_Exception
			  is 
			     Class_Definition
			  end __Class_Name ';' 
			  dollardStdClass_End
			;
/*==== Embeded Class =======================================================*/
Embeded_C_Declaration	: Embeded_C_Declaration_1
			| private dollardSet_Priv Embeded_C_Declaration_1 
			| protected dollardSet_Prot Embeded_C_Declaration_1 
			;
Embeded_C_Declaration_1	: Generic_C_Instanciation
			| NoGeneric_C_Declaration
			| Inc_NoGeneric_Class
			; 
/*==== Class Definition ====================================================*/
Class_Definition	: __Member_Method_List
			  __Field_Declaration
			  __Friends_Declaration
			;

Generic_Type_List	: Generic_Type 
			| Generic_Type_List ';' Generic_Type 
			;

Generic_Type		: IDENTIFIER { Set_Item($1); } as Type_Constraint 
			;

Type_Constraint		: any dollardSet_Any dollardAdd_GenType
			| Type_Name dollardAdd_GenType
			| Type_Name '(' dollardDynaType_Begin Type_List ')'
					dollardAdd_InstType dollardAdd_DynaGenType
			;
__Inherits_Classes	: Empty
			| inherits Type_List
			;
__Uses_Classes		: Empty
			| uses Type_List
			;
__Raises_Exception	: Empty
			| raises Type_List dollardAdd_Raises 
			;
__Embeded_Class_List	: Empty
			| Embeded_Class_List 
			;
Embeded_Class_List	: Embeded_C_Declaration dollardAdd_Embeded
			| Embeded_Class_List Embeded_C_Declaration dollardAdd_Embeded
			;
__Class_Name		: Empty
			| Type_Name 
			;
Type_List		: Type_Name { Add_Type(); }
			| Type_List ',' Type_Name { Add_Type(); }
			;
Type_Name		: IDENTIFIER { Type_Name($1); Type_Pack_Blanc(); }
			| IDENTIFIER CDL_from { Type_Name($1); }  Package_Name { Type_Pack($4); }
			;

/*==========================================================================*/
/*==== Exception Definition ================================================*/
/*==========================================================================*/
Exception_Declaration	: exception dollardset_inc_state Type_Name dollardrestore_state dollardExcept_Begin
				inherits 
				Type_List
			  ';' 
			  dollardExcept_End
			;

/*==========================================================================*/
/*==== Alias Definition ====================================================*/
/*==========================================================================*/
Alias_Declaration	: alias dollardset_inc_state Type_Name dollardrestore_state dollardAlias_Begin
				is 
				Type_Name dollardAlias_Type
			  ';' 
			  dollardAlias_End
			;

/*==========================================================================*/
/*==== Pointer Definition ====================================================*/
/*==========================================================================*/
Pointer_Declaration	: pointer dollardset_inc_state Type_Name dollardrestore_state dollardPointer_Begin
			  CDL_to 
				Type_Name dollardPointer_Type 
			  ';' 
			  dollardPointer_End
			;

/*==========================================================================*/
/*==== Imported Definition =================================================*/
/*==========================================================================*/
Imported_Declaration	: imported  dollardset_inc_state Type_Name dollardrestore_state dollardImported_Begin ';' dollardImported_End
			;

/*==========================================================================*/
/*==== Imported Definition =================================================*/
/*==========================================================================*/
Primitive_Declaration	: primitive dollardset_inc_state Type_Name dollardrestore_state dollardPrim_Begin 
			  __Inherits_Classes
			  ';' 
			  dollardPrim_End
			;

/*==========================================================================*/
/*==== Enumeration Definition ==============================================*/
/*==========================================================================*/
Enumeration_Declaration	: enumeration dollardset_inc_state Type_Name dollardrestore_state dollardEnum_Begin
			  is
			     Enum_Item_List
			  __Enumeration_End 
			  ';' 
			  dollardEnum_End
			;
__Enumeration_End	: Empty
			| Enumeration_End
			;
Enumeration_End		: end 
			| end Type_Name
			;
Enum_Item_List		: IDENTIFIER { Add_Enum($1); }
			| Enum_Item_List ',' IDENTIFIER { Add_Enum($3); }
			;
/*==========================================================================*/
/*==== Method Definition ===================================================*/
/*==========================================================================*/
__Member_Method_List	: Empty
			| Member_Method_List { add_cpp_comment_to_method(); }
			;
Member_Method_List	: Member_Method
			| Member_Method_List Member_Method
			;
Member_Method		: Method_Name Method_Definition
			;
Method_Name		: IDENTIFIER { Set_Method($1); } 
			;
Method_Definition	: Constructor
			| Instance_Method
			| Class_Method
			;
__Extern_Method_List	: Empty
			| Extern_Method_List { add_cpp_comment_to_method(); }
			;
Extern_Method_List	: Extern_Method
			| Extern_Method_List Extern_Method
			;
Extern_Method		: Method_Name Extern_Met_Definition
			;
Extern_Met_Definition	: Extern_Met
			;
/*==== Methods definitions =================================================*/
Constructor		: Constructor_Header 
			    __Constructed_Type_Dec
			    __Errors_Declaration
			    __Scoop_Declaration
			  ';' 
			  dollardMemberMet_End
			;
Instance_Method		: Instance_Method_Header
			    __Returnrd_Type_Dec
			    __Errors_Declaration
			    __Inst_Met_Attr_Dec
			  ';'
			  dollardMemberMet_End
			;
Class_Method		: Class_Method_Header
			    __Returnrd_Type_Dec
			    __Errors_Declaration
			    __Scoop_Declaration
			  ';'
			  dollardMemberMet_End
			;
Extern_Met		: Extern_Method_Header
			    __Returnrd_Type_Dec
			    __Errors_Declaration
			    __Scoop_Declaration
			  ';'
			  dollardExternMet_End
			;
/*==== The Headers =========================================================*/
Constructor_Header	: Empty dollardConstruct_Begin 
			| '(' dollardConstruct_Begin Parameter_List ')' 
			;
Extern_Method_Header	: Empty dollardExtMet_Begin dollardEnd
			| '(' dollardExtMet_Begin Parameter_List ')' 
			;
Instance_Method_Header	: '(' me dollardInstMet_Begin  __Me_Mode dollardAdd_Me 
						__S_Parameter_List ')'
			;
Class_Method_Header	: '(' myclass dollardClassMet_Begin __S_Parameter_List ')'
			;

Friend_Const_Header	: Empty dollardFriend_Construct_Begin 
			| '(' dollardFriend_Construct_Begin Parameter_List ')' 
			;
Friend_ExtMet_Header	: Empty dollardFriend_ExtMet_Begin dollardEnd
			| '(' dollardFriend_ExtMet_Begin Parameter_List ')' 
			;
Friend_InstMet_Header	: '(' me dollardFriend_InstMet_Begin  __Me_Mode dollardAdd_Me 
						__S_Parameter_List ')'
			;
Friend_ClassMet_Header	: '(' myclass dollardFriend_ClassMet_Begin 
						__S_Parameter_List ')'
			;

/*==========================================================================*/
/*==== Parameter Definition ================================================*/
/*==========================================================================*/
__S_Parameter_List	: Empty
			| ';' Parameter_List 
			;
Parameter_List		: Parameter 
			| Parameter_List ';' Parameter 
			;
Parameter		: Name_List ':' Passage_Mode 
			       Transmeted_Type dollardParam_Begin
			;
/*==== The Parameter Mode ==================================================*/
Passage_Mode		: __In
			| out dollardSet_Out
			| in out dollardSet_InOut 
			;
__In			: Empty
			| in dollardSet_In
			;
__Me_Mode		: Empty
			| ':' Passage_Mode __Acces_Mode 
			;
__Acces_Mode		: Empty
			| Acces_Mode 
			;
Acces_Mode		: mutable    dollardSet_Mutable
			| any	     dollardSet_Mutable_Any
			| immutable  dollardSet_Immutable
			;


/*==========================================================================*/
/*==== Type Definition =====================================================*/
/*==========================================================================*/
/* 			| like IDENTIFIER  dollardSet_Like_Type;  */

Transmeted_Type		: __Acces_Mode Associated_Type 
			| __Acces_Mode Type_Name __Initial_Value
			;
Constructed_Type	: __Acces_Mode Type_Name dollardAdd_Returns
			;
Returned_Type		: __Acces_Mode Type_Name dollardAdd_Returns
			| __Acces_Mode Associated_Type dollardAdd_Returns
			;
Associated_Type		: like me dollardSet_Like_Me
			;
__Initial_Value		: Empty
			|'=' Initial_Value 
			;
Initial_Value		: INTEGER    { Add_Value($1,INTEGER); }
			| REAL       { Add_Value($1,REAL); }
			| STRING     { Add_Value($1,STRING); }
			| LITERAL    { Add_Value($1,LITERAL); }
			| IDENTIFIER { Add_Value($1,IDENTIFIER); }
			;

__Returnrd_Type_Dec	: Empty
			| returns Returned_Type 
			;
__Constructed_Type_Dec	: Empty
			| returns Constructed_Type 
			;
__Errors_Declaration	: Empty
			| raises Type_List dollardAdd_MetRaises
			;
__Inst_Met_Attr_Dec	: Empty dollardSet_Static
			| is Inst_Met_Attr_Dec 
			;
Inst_Met_Attr_Dec	: Scoop  
			| Definition_Level __Scoop
			;
Definition_Level	: Redefinition
			| redefined dollardSet_Redefined __Redefinition 
			;
__Redefinition		: Empty dollardSet_Virtual
			| Redefinition
			;
Redefinition		: statiC dollardSet_Static
			| deferred dollardSet_Defe dollardSet_Virtual
                        | virtual dollardSet_Virtual
			;
	
/*==========================================================================*/
/*==== Field Definition ====================================================*/
/*==========================================================================*/
__Field_Declaration	: Empty
			| fields 
			  Field_List  
			;
Field_List		: Field
			| Field_List Field
			;
Field			: Name_List ':' Type_Name  
				__Field_Dimension
			        __Scoop_Pro_Declaration 
			  ';' dollardAdd_Field
			;
__Field_Dimension	: Empty
			| '[' Integer_List ']'
			;

Integer_List		: INTEGER { Begin_List_Int($1); }
			| Integer_List ',' INTEGER  { Make_List_Int($3); }
			;
/*==========================================================================*/
/*==== Friend Definition ===================================================*/
/*==========================================================================*/
__Friends_Declaration	: Empty
			| friends { CDL_MustNotCheckUses(); } Friend_List { CDL_MustCheckUses(); }
			;
Friend_List		: Friend 
			| Friend_List ','  Friend
			;
Friend			: Friend_Method_Dec 
			| class Type_Name   dollardAdd_Friend_Class
			;
Friend_Method_Dec	: Method_Name Friend_Method 
			;
Friend_Method		: CDL_from __Class Type_Name { Method_TypeName(); } Friend_Method_Type_Dec
			| CDL_from package IDENTIFIER Friend_ExtMet_Header 
						{ Add_FriendExtMet($3); }
			;

Friend_Method_Type_Dec  : Friend_InstMet_Header dollardAdd_FriendMet
			| Friend_ClassMet_Header dollardAdd_FriendMet
			| Friend_Const_Header	dollardAdd_FriendMet
			;
__Class			: Empty
			| class
			;

/*==========================================================================*/
/*==== Scoop Definition ====================================================*/
/*==========================================================================*/
__Scoop_Declaration	: Empty
			| is Scoop 
			;
__Scoop_Pro_Declaration	: Empty
			| is protected dollardSet_Prot
			;
__Scoop			: Empty
			| Scoop
			;
Scoop			: private dollardSet_Priv
			| protected dollardSet_Prot
                        ;
/*==========================================================================*/
/*==== Others ==============================================================*/
/*==========================================================================*/
Name_List		: IDENTIFIER { add_name_to_list($1); }
			| Name_List ',' IDENTIFIER { add_name_to_list($3); }
			;
Empty			:	
			;
Empty_Str		: {$$[0] = '\0';}
			;
/*==========================================================================*/
/*==== The actions =========================================================*/
/*==========================================================================*/

/*=-=-=-=-=-=-=-=-= Manage the names and the lists =-=-=-=-=-=-=-=-=-=-=-=-=*/
dollardset_inc_state          : {set_inc_state();}
dollardrestore_state          : {restore_state();}
/*=-=-=-=-=-=-=-=-= The actions for the Interface  =-=-=-=-=-=-=-=-=-=-=-=-=*/
dollardInterface_Class	: {Interface_Class();}
dollardInterface_End		: {Interface_End();}
/*=-=-=-=-=-=-=-=-= The actions for the Engine   =-=-=-=-=-=-=-=-=-=-=-=-=-=*/
dollardEngine_End		: {Engine_End();}
/*=-=-=-=-=-=-=-=-= The actions for the classes  =-=-=-=-=-=-=-=-=-=-=-=-=-=*/
dollardAlias_Begin		: {Alias_Begin();}
dollardAlias_Type		: {Alias_Type();}
dollardAlias_End		: {Alias_End();}
/*--------------------------------------------------------------------------*/
dollardPointer_Begin		: {Pointer_Begin();}
dollardPointer_Type		: {Pointer_Type();}
dollardPointer_End		: {Pointer_End();}
/*--------------------------------------------------------------------------*/
dollardImported_Begin		: {Imported_Begin();}
dollardImported_End		: {Imported_End();}
/*--------------------------------------------------------------------------*/
dollardPrim_Begin		: {Prim_Begin();}
dollardPrim_End		: {Prim_End();}
/*--------------------------------------------------------------------------*/
dollardExcept_Begin		: {Except_Begin();}
dollardExcept_End		: {Except_End();}
/*--------------------------------------------------------------------------*/
dollardEnum_Begin		: {Enum_Begin();}
dollardEnum_End		: {Enum_End();}
/*--------------------------------------------------------------------------*/
dollardInc_Class_Dec		: {Inc_Class_Dec();}
dollardInc_GenClass_Dec	: {Inc_GenClass_Dec();}
/*--------------------------------------------------------------------------*/
dollardGenClass_Begin		: {GenClass_Begin();}
dollardAdd_GenType		: {Add_GenType();}
dollardAdd_DynaGenType	: {Add_DynaGenType();}
dollardAdd_Embeded		: {Add_Embeded();}
dollardGenClass_End		: {GenClass_End();}
/*--------------------------------------------------------------------------*/
dollardInstClass_Begin	: {InstClass_Begin();}
dollardAdd_Gen_Class		: {Add_Gen_Class();}
dollardAdd_InstType		: {Add_InstType();}
dollardInstClass_End		: {InstClass_End();}
dollardDynaType_Begin		: {DynaType_Begin();}
/*--------------------------------------------------------------------------*/
dollardStdClass_Begin		: {StdClass_Begin();}
dollardStdClass_End		: {StdClass_End();}
/*--------------------------------------------------------------------------*/
dollardAdd_Raises		: {Add_Raises();}
dollardAdd_Field		: {Add_Field();}
dollardAdd_FriendMet		: {Add_FriendMet();}
dollardAdd_Friend_Class	: {Add_Friend_Class();}
/*=-=-=-=-=-=-=-=-= The actions for the Methods  =-=-=-=-=-=-=-=-=-=-=-=-=-=*/
dollardConstruct_Begin	: {Construct_Begin();}
dollardInstMet_Begin		: {InstMet_Begin();}
dollardClassMet_Begin		: {ClassMet_Begin();}
dollardExtMet_Begin		: {ExtMet_Begin();}
dollardFriend_Construct_Begin	: {Friend_Construct_Begin();}
dollardFriend_InstMet_Begin	: {Friend_InstMet_Begin();}
dollardFriend_ClassMet_Begin	: {Friend_ClassMet_Begin();}
dollardFriend_ExtMet_Begin	: {Friend_ExtMet_Begin();}
dollardAdd_Me			: {Add_Me();}
dollardAdd_MetRaises		: {Add_MetRaises();}
dollardAdd_Returns		: {Add_Returns();}
dollardMemberMet_End		: {MemberMet_End();}
dollardExternMet_End		: {ExternMet_End();}
/*=-=-=-=-=-=-=-=-= The actions for Parameteres  =-=-=-=-=-=-=-=-=-=-=-=-=-=*/
dollardParam_Begin		: {Param_Begin();}
/*=-=-=-=-=-=-=-=-= The general actions  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
dollardEnd			: {End();}
dollardSet_In			: {Set_In();}
dollardSet_Out		: {Set_Out();}
dollardSet_InOut		: {Set_InOut();}
dollardSet_Mutable		: {Set_Mutable();}
dollardSet_Mutable_Any	: {Set_Mutable_Any();}
dollardSet_Immutable		: {Set_Immutable();}
dollardSet_Priv		: {Set_Priv();}
dollardSet_Defe		: {Set_Defe();}
dollardSet_Redefined		: {Set_Redefined();}
dollardSet_Prot		: {Set_Prot();}
dollardSet_Static		: {Set_Static();}
dollardSet_Virtual		: {Set_Virtual();}
dollardSet_Like_Me		: {Set_Like_Me();}
dollardSet_Any		: {Set_Any();}
%%
