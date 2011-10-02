
#ifdef __cplusplus
extern "C"
{
#endif

typedef struct _edlstring {
	char *str;
	int  length;
} edlstring;

void edl_set_var(const edlstring,const edlstring);
void edl_set_varvar(const edlstring,const edlstring);
void edl_set_varevalvar(const edlstring,const edlstring);
void edl_set_pvar(const edlstring,const edlstring);
void edl_set_pvarvar(const edlstring,const edlstring);
void edl_set_pvarevalvar(const edlstring,const edlstring);
void edl_unset_var(const edlstring);
void edl_unset_pvar(const edlstring);
void edl_test_condition(const edlstring,int,const edlstring);
void edl_eval_condition();
void edl_eval_local_condition(int);
void edl_clear_execution_status(); 
void edl_cout();
void edl_create_string_var(const edlstring);
void edl_printlist_add_var(const edlstring);
void edl_printlist_addps_var(const edlstring);
void edl_printlist_add_str(const edlstring);
void edl_clear_printlist();
void edl_create_template(const edlstring);
void edl_set_template(const edlstring);
void edl_clear_template(const edlstring);
void edl_add_to_template(const edlstring);
void edl_end_template();
void edl_apply_template(const edlstring);
void edl_add_to_varlist(const edlstring);
void edl_end_apply(const edlstring);
void edl_open_library(const edlstring);
void edl_close_library(const edlstring);
void edl_call_function_library(const edlstring, const edlstring, const edlstring);
void edl_call_procedure_library(const edlstring, const edlstring);
void edl_arglist_add_var(const edlstring);
void edl_arglist_add_str(const edlstring);
unsigned int edl_must_execute();
void edl_open_file(const edlstring,const edlstring);
void edl_write_file(const edlstring,const edlstring);
void edl_close_file(const edlstring);
void edl_set_varname();
void edl_set_str();
void edl_add_include_directory(const edlstring);
void edl_uses(const edlstring);
void edl_uses_var(const edlstring);
void edl_isvardefined(const edlstring);
void edl_isvarnotdefined(const edlstring);
void edl_else_execution_status();
void edl_fileexist(const edlstring);
void edl_filenotexist(const edlstring);
void edl_fileexist_var(const edlstring);
void edl_filenotexist_var(const edlstring);

void edl_isvardefinedm(const edlstring);
void edl_isvarnotdefinedm(const edlstring);
void edl_fileexistm(const edlstring);
void edl_filenotexistm(const edlstring);
void edl_fileexist_varm(const edlstring);
void edl_filenotexist_varm(const edlstring);

void EDL_SetCurrentFile( const edlstring);

edlstring edl_strdup(const char *, const int );
edlstring edl_string(const char *, const int );
void edlstring_free(const edlstring );

#ifdef __cplusplus
}
#endif
