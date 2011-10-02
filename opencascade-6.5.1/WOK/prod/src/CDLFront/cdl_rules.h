/*============================================================================
//== File: cdl_rules.h               Version 1.2
//==
//== SCCS	Date: 17/11/93
//===========================================================================*/

void Clear_ListOfItem();
void Type_Name(char*);
void Type_Pack(char*);	        
void Type_Pack_Blanc();          
void Add_Type();
void add_name_to_list(char*);
void Begin_List_Ident();	        
void Make_List_Ident();         
void Begin_List_Int(char*);           
void Make_List_Int(char*);	       
void set_inc_state();
void restore_state();
void add_cpp_comment(int,char*);
void add_documentation(char*);
void add_documentation1(char*);

/*=-=-=-=-=-=-=-=-= The actions for the Package  =-=-=-=-=-=-=-=-=-=-=-=-=-=*/
void Pack_Begin(char*);	       
void Pack_Use(char*);		       
void Pack_End();		       
/*=-=-=-=-=-=-=-=-= The actions for the Schema   =-=-=-=-=-=-=-=-=-=-=-=-=-=*/
void Schema_Begin(char*);	       
void Schema_Package(char*);	       
void Schema_Class();	       
void Schema_End();	       
/*=-=-=-=-=-=-=-=-= The actions for the Interface  =-=-=-=-=-=-=-=-=-=-=-=-=*/
void Interface_Begin(char*);	       
void Interface_Name_Check();    
void Interface_Use(char*);	       
void Interface_Package(char*);       
void Interface_Class();	       
void Interface_Method(char*);	       
void Interface_End();	       
/*=-=-=-=-=-=-=-=-= The actions for the Engine   =-=-=-=-=-=-=-=-=-=-=-=-=-=*/
void Engine_Begin(char *);	       
void Engine_Name_Check();       
void Engine_Schema(char*);	       
void Engine_Interface(char*);	       
void Engine_End();	
/*=-=-=-=-=-=-=-=-= The actions for the Component   =-=-=-=-=-=-=-=-=-=-=-=-=-=*/
void Component_Begin(char *);	       
void Component_Name_Check();       
void Component_Interface(char*,char*);	       
void Component_End();	       
/*=-=-=-=-=-=-=-=-= The actions for the ExecFile   =-=-=-=-=-=-=-=-=-=-=-=*/
void Executable_Begin(char*);	       
void ExecFile_Begin(char*);	       
void ExecFile_Schema(char*);
void ExecFile_AddUse(char*);
void ExecFile_SetUseType(int);
void ExecFile_AddComponent(char*);
void ExecFile_SetLang(int);
void ExecFile_End();	  
void Executable_End();	       
/*=-=-=-=-=-=-=-=-= The actions for the classes  =-=-=-=-=-=-=-=-=-=-=-=-=-=*/
void Alias_Begin();	       
void Alias_Type();	       
void Alias_End();	       
/*--------------------------------------------------------------------------*/
void Pointer_Begin();	       
void Pointer_Type();	       
void Pointer_End();	       
/*--------------------------------------------------------------------------*/
void Imported_Begin();	       
void Imported_End();	       
/*--------------------------------------------------------------------------*/
void Prim_Begin();	       
void Prim_End();	               
/*--------------------------------------------------------------------------*/
void Except_Begin();	       
void Except_End();	       
/*--------------------------------------------------------------------------*/
void Enum_Begin();	       
void Add_Enum(char*);		       
void Enum_Name_Check();	       
void Enum_End();		       
/*--------------------------------------------------------------------------*/
void Inc_Class_Dec();	       
void Inc_GenClass_Dec();	       
/*--------------------------------------------------------------------------*/
void GenClass_Begin();	       
void Add_GenType();	       
void Add_DynaGenType();	       
void Add_Embeded();	
void GenClass_End();	       
/*--------------------------------------------------------------------------*/
void InstClass_Begin();         
void Add_Gen_Class();	       
void Add_InstType();	       
void InstClass_End();	       
void DynaType_Begin();          
/*--------------------------------------------------------------------------*/
void StdClass_Begin();          
void Add_Std_Ancestors();
void Add_Std_Uses();
void StdClass_End();	       
/*--------------------------------------------------------------------------*/
void Add_Inherits();	       
void Add_Uses();	       	       
void Add_Raises();	       
void Add_Field();	       
void Add_RedefField();          
void Add_FriendMet();	       
void Add_FriendExtMet(char*);        
void Add_Friend_Class();	       
/*=-=-=-=-=-=-=-=-= The actions for the Methods  =-=-=-=-=-=-=-=-=-=-=-=-=-=*/
void add_cpp_comment_to_method(); 
void Construct_Begin();         
void InstMet_Begin();	       
void ClassMet_Begin();          
void ExtMet_Begin();	       
void Method_TypeName();
void Friend_Construct_Begin();  
void Friend_InstMet_Begin();  
void Friend_ClassMet_Begin();   
void Friend_ExtMet_Begin();     
void Add_Me();	       	       
void Add_MetRaises();	       
void Add_Returns();	       
void MemberMet_End();           
void ExternMet_End();           
/*=-=-=-=-=-=-=-=-= The actions for Parameteres  =-=-=-=-=-=-=-=-=-=-=-=-=-=*/
void Param_Begin();	     
void Add_Value(char*,int);	       
/*=-=-=-=-=-=-=-=-= The general actions  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
void End();		       
void Set_In();	       	       
void Set_Out();	       	       
void Set_InOut();	       
void Set_Mutable();	       
void Set_Mutable_Any();         
void Set_Immutable();	       
void Set_Priv();	       	       
void Set_Defe();	       	       
void Set_Redefined();	       
void Set_Prot();	               
void Set_Static();	       
void Set_Virtual();	       
void Set_Method(char*);	       
void Set_Like_Me();	       
void Set_Like_Type();	       
void Set_Item(char *);	       	       
void Set_Any();	       	       
void CDL_MustCheckUses();
void CDL_MustNotCheckUses();
void Client_Begin(char *);	       
void Client_Interface(char*);	       
void Client_Method(char*,int);	       
void Client_Use(char*);
void Client_End();	
