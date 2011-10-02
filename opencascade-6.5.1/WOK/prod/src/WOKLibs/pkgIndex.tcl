global tcl_platform

###########################################
# LINUX
###########################################

if { $tcl_platform(os) == "Linux" }  {
package ifneeded Woktools 2.0 "tclPkgSetup $dir/lin Woktools 2.0 {
                                        {libwoktoolscmd.so load {
					    msgprint msgisset msgissetcmd msgissetlong msgset msgsetcmd 
					    msgsetlong msgunset msgunsetcmd msgunsetlong msgsetheader 
					    msgunsetheader msgissetheader msginfo}}}"

package ifneeded Wokutils 2.0 "tclPkgSetup $dir Wokutils 2.0 {
    {libwokutilscmd.so load { wokcmp} } }"

package ifneeded Wok 2.0 "package require Woktools; 
                             tclPkgSetup $dir/lin Wok 2.0 {
				 {libwokcmd.so load {
				     Sinfo Wcreate Winfo Wrm Wdeclare fcreate finfo frm pinfo screate 
				     sinfo srm ucreate uinfo umpmake umake urm w_info wcreate 
				     wokcd wokclose wokinfo wokparam wokprofile wokenv wrm wmove 
				     stepinputaddstepinputinfo stepoutputadd stepoutputinfo stepaddexecdepitem }}}"

package ifneeded Ms 2.0 "package require Woktools; 
                             tclPkgSetup $dir/lin Ms 2.0 {
				 {libmscmd.so load {
				     mscheck msclear msclinfo msextract msgeninfo msinfo msinstinfo 
				     msmmthinfo msmthinfo mspkinfo msschinfo msrm msstdinfo 
				     mstranslate msxmthinfo}}}"


				 }

###########################################
# SOLARIS
###########################################

if { $tcl_platform(os) == "SunOS" }  {
package ifneeded Woktools 2.0 "tclPkgSetup $dir/sun Woktools 2.0 {
                                        {libwoktoolscmd.so load {
					    msgprint msgisset msgissetcmd msgissetlong msgset msgsetcmd 
					    msgsetlong msgunset msgunsetcmd msgunsetlong msgsetheader 
					    msgunsetheader msgissetheader msginfo}}}"

package ifneeded Wokutils 2.0 "tclPkgSetup $dir Wokutils 2.0 {
    {libwokutilscmd.so load { wokcmp} } }"

package ifneeded Wok 2.0 "package require Woktools; 
                             tclPkgSetup $dir/sun Wok 2.0 {
				 {libwokcmd.so load {
				     Sinfo Wcreate Winfo Wrm Wdeclare fcreate finfo frm pinfo screate 
				     sinfo srm ucreate uinfo umpmake umake urm w_info wcreate 
				     wokcd wokclose wokinfo wokparam wokprofile wokenv wrm wmove 
				     stepinputaddstepinputinfo stepoutputadd stepoutputinfo stepaddexecdepitem }}}"

package ifneeded Ms 2.0 "package require Woktools; 
                             tclPkgSetup $dir/sun Ms 2.0 {
				 {libmscmd.so load {
				     mscheck msclear msclinfo msextract msgeninfo msinfo msinstinfo 
				     msmmthinfo msmthinfo mspkinfo msschinfo msrm msstdinfo 
				     mstranslate msxmthinfo}}}"


				 }

###########################################
# IRIX
###########################################

if { $tcl_platform(os) == "IRIX64" || $tcl_platform(os) == "IRIX" }  {
package ifneeded Woktools 2.0 "tclPkgSetup $dir/sil Woktools 2.0 {
                                        {libwoktoolscmd.so load {
					    msgprint msgisset msgissetcmd msgissetlong msgset msgsetcmd 
					    msgsetlong msgunset msgunsetcmd msgunsetlong msgsetheader 
					    msgunsetheader msgissetheader msginfo}}}"

package ifneeded Wokutils 2.0 "tclPkgSetup $dir Wokutils 2.0 {
    {libwokutilscmd.so load { wokcmp} } }"

package ifneeded Wok 2.0 "package require Woktools; 
                             tclPkgSetup $dir/sil Wok 2.0 {
				 {libwokcmd.so load {
				     Sinfo Wcreate Winfo Wrm Wdeclare fcreate finfo frm pinfo screate 
				     sinfo srm ucreate uinfo umpmake umake urm w_info wcreate 
				     wokcd wokclose wokinfo wokparam wokprofile wokenv wrm wmove 
				     stepinputaddstepinputinfo stepoutputadd stepoutputinfo stepaddexecdepitem }}}"

package ifneeded Ms 2.0 "package require Woktools; 
                             tclPkgSetup $dir/sil Ms 2.0 {
				 {libmscmd.so load {
				     mscheck msclear msclinfo msextract msgeninfo msinfo msinstinfo 
				     msmmthinfo msmthinfo mspkinfo msschinfo msrm msstdinfo 
				     mstranslate msxmthinfo}}}"


				 }

###########################################
# WINDOWS
###########################################

if { $tcl_platform(platform) == "windows" }  {
package ifneeded Woktools 2.0 "tclPkgSetup $dir/wnt Woktools 2.0 {
                                        {woktoolscmd.dll load {
					    msgprint msgisset msgissetcmd msgissetlong msgset msgsetcmd 
					    msgsetlong msgunset msgunsetcmd msgunsetlong msgsetheader 
					    msgunsetheader msgissetheader msginfo}}}"

package ifneeded Wokutils 2.0 "tclPkgSetup $dir/wnt Wokutils 2.0 {
    {wokutilscmd.dll load { wokcmp wokfind} } }"

package ifneeded Wok 2.0 "package require Woktools; 
                             tclPkgSetup $dir/wnt Wok 2.0 {
				 {wokcmd.dll load {
				     Sinfo Wcreate Winfo Wrm Wdeclare fcreate finfo frm pinfo screate 
				     sinfo srm ucreate uinfo umpmake umake urm w_info wcreate wprocess
				     wokcd wokclose wokinfo wokparam wokprofile wokenv wrm wmove 
				     stepinputaddstepinputinfo stepoutputadd stepoutputinfo stepaddexecdepitem }}}"

package ifneeded Ms 2.0 "package require Woktools; 
                             tclPkgSetup $dir/wnt Ms 2.0 {
				 {mscmd.dll load {
				     mscheck msclear msclinfo msextract msgeninfo msinfo msinstinfo 
				     msmmthinfo msmthinfo mspkinfo msschinfo msrm msstdinfo 
				     mstranslate msxmthinfo}}}"

				 }

###########################################
# MACOS
###########################################

if { $tcl_platform(os) == "Darwin" }  {
package ifneeded Woktools 2.0 "tclPkgSetup $dir/mac Woktools 2.0 {
                                        {libwoktoolscmd.dylib load {
					    msgprint msgisset msgissetcmd msgissetlong msgset msgsetcmd 
					    msgsetlong msgunset msgunsetcmd msgunsetlong msgsetheader 
					    msgunsetheader msgissetheader msginfo}}}"

package ifneeded Wokutils 2.0 "tclPkgSetup $dir/mac Wokutils 2.0 {
    {libwokutilscmd.dylib load { wokcmp} } }"

package ifneeded Wok 2.0 "package require Woktools; 
                             tclPkgSetup $dir/mac Wok 2.0 {
				 {libwokcmd.dylib load {
				     Sinfo Wcreate Winfo Wrm Wdeclare fcreate finfo frm pinfo screate 
				     sinfo srm ucreate uinfo umpmake umake urm w_info wcreate 
				     wokcd wokclose wokinfo wokparam wokprofile wokenv wrm wmove 
				     stepinputaddstepinputinfo stepoutputadd stepoutputinfo stepaddexecdepitem }}}"

package ifneeded Ms 2.0 "package require Woktools; 
                             tclPkgSetup $dir/mac Ms 2.0 {
				 {libmscmd.dylib load {
				     mscheck msclear msclinfo msextract msgeninfo msinfo msinstinfo 
				     msmmthinfo msmthinfo mspkinfo msschinfo msrm msstdinfo 
				     mstranslate msxmthinfo}}}"


				 }

###########################################
# FreeBSD
###########################################

if { $tcl_platform(os) == "FreeBSD" }  {
package ifneeded Woktools 2.0 "tclPkgSetup $dir/bsd Woktools 2.0 {
                                        {libwoktoolscmd.so load {
					    msgprint msgisset msgissetcmd msgissetlong msgset msgsetcmd
					    msgsetlong msgunset msgunsetcmd msgunsetlong msgsetheader
					    msgunsetheader msgissetheader msginfo}}}"

package ifneeded Wokutils 2.0 "tclPkgSetup $dir Wokutils 2.0 {
    {libwokutilscmd.so load { wokcmp} } }"

package ifneeded Wok 2.0 "package require Woktools;
                             tclPkgSetup $dir/bsd Wok 2.0 {
				 {libwokcmd.so load {
				     Sinfo Wcreate Winfo Wrm Wdeclare fcreate finfo frm pinfo screate
				     sinfo srm ucreate uinfo umpmake umake urm w_info wcreate
				     wokcd wokclose wokinfo wokparam wokprofile wokenv wrm wmove
				     stepinputaddstepinputinfo stepoutputadd stepoutputinfo stepaddexecdepitem }}}"

package ifneeded Ms 2.0 "package require Woktools;
                             tclPkgSetup $dir/bsd Ms 2.0 {
				 {libmscmd.so load {
				     mscheck msclear msclinfo msextract msgeninfo msinfo msinstinfo
				     msmmthinfo msmthinfo mspkinfo msschinfo msrm msstdinfo
				     mstranslate msxmthinfo}}}"


				 }
