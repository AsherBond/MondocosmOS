/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath"
 * exception as provided by Sun in the License file that accompanied
 * this code.
 */
package org.jdesktop.wonderland.modules.xremwin.client;

import java.awt.event.KeyEvent;
import org.jdesktop.wonderland.common.StableAPI;

/**
 * Converts Java key codes to X11 keysyms.
 */

@StableAPI
class KeycodeToKeysym {

    /* Handcrafted table with help from emacs.  The main source file
     * for this was keysymdef.h from X11.  I merely guessed at the VK
     * names and did some hand massaging after the fact.  The lines
     * that are commented out are conversions that didn't seem to
     * have a match.  I left them in here for completeness.
     *
     * The format of each line is as follows: {keysym, keycode}
     *
     * [[[WDW - FIXME: For quicker searching, this table should be
     * sorted in order of Java keycode.]]]
     */
    private final static int[][] map = {
        /* XK_BackSpace         */      { 0xFF08, KeyEvent.VK_BACK_SPACE },
        /* XK_Tab               */      { 0xFF09, KeyEvent.VK_TAB },
//      /* XK_Linefeed          */      { 0xFF0A, KeyEvent.VK_LINEFEED },
        /* XK_Clear             */      { 0xFF0B, KeyEvent.VK_CLEAR },
        /* XK_Return            */      { 0xFF0D, KeyEvent.VK_ENTER },
        /* XK_Pause             */      { 0xFF13, KeyEvent.VK_PAUSE },
        /* XK_Scroll_Lock       */      { 0xFF14, KeyEvent.VK_SCROLL_LOCK },
//      /* XK_Sys_Req           */      { 0xFF15, KeyEvent.VK_SYS_REQ },
        /* XK_Escape            */      { 0xFF1B, KeyEvent.VK_ESCAPE },
        /* XK_Delete            */      { 0xFFFF, KeyEvent.VK_DELETE },
        /* XK_Home              */      { 0xFF50, KeyEvent.VK_HOME },
        /* XK_Left              */      { 0xFF51, KeyEvent.VK_LEFT },
        /* XK_Up                */      { 0xFF52, KeyEvent.VK_UP },
        /* XK_Right             */      { 0xFF53, KeyEvent.VK_RIGHT },
        /* XK_Down              */      { 0xFF54, KeyEvent.VK_DOWN },
//      /* XK_Prior             */      { 0xFF55, KeyEvent.VK_PRIOR },
        /* XK_Page_Up           */      { 0xFF55, KeyEvent.VK_PAGE_UP },
//      /* XK_Next              */      { 0xFF56, KeyEvent.VK_NEXT },
        /* XK_Page_Down         */      { 0xFF56, KeyEvent.VK_PAGE_DOWN },
        /* XK_End               */      { 0xFF57, KeyEvent.VK_END },
//      /* XK_Begin             */      { 0xFF58, KeyEvent.VK_BEGIN },
//      /* XK_Select            */      { 0xFF60, KeyEvent.VK_SELECT },
        /* XK_Print             */      { 0xFF61, KeyEvent.VK_PRINTSCREEN },
//      /* XK_Execute           */      { 0xFF62, KeyEvent.VK_EXECUTE },
        /* XK_Insert            */      { 0xFF63, KeyEvent.VK_INSERT },
        /* XK_Undo              */      { 0xFF65, KeyEvent.VK_UNDO },
//      /* XK_Redo              */      { 0xFF66, KeyEvent.VK_REDO },
//      /* XK_Menu              */      { 0xFF67, KeyEvent.VK_MENU },
        /* XK_Find              */      { 0xFF68, KeyEvent.VK_FIND },
        /* XK_Cancel            */      { 0xFF69, KeyEvent.VK_CANCEL },
        /* XK_Help              */      { 0xFF6A, KeyEvent.VK_HELP },
//      /* XK_Break             */      { 0xFF6B, KeyEvent.VK_BREAK },
        /* XK_Mode_switch       */      { 0xFF7E, KeyEvent.VK_MODECHANGE },
//      /* XK_script_switch     */      { 0xFF7E, KeyEvent.VK_SCRIPT_SWITCH },
        /* XK_Num_Lock          */      { 0xFF7F, KeyEvent.VK_NUM_LOCK },
        /* XK_F1                */      { 0xFFBE, KeyEvent.VK_F1 },
        /* XK_F2                */      { 0xFFBF, KeyEvent.VK_F2 },
        /* XK_F3                */      { 0xFFC0, KeyEvent.VK_F3 },
        /* XK_F4                */      { 0xFFC1, KeyEvent.VK_F4 },
        /* XK_F5                */      { 0xFFC2, KeyEvent.VK_F5 },
        /* XK_F6                */      { 0xFFC3, KeyEvent.VK_F6 },
        /* XK_F7                */      { 0xFFC4, KeyEvent.VK_F7 },
        /* XK_F8                */      { 0xFFC5, KeyEvent.VK_F8 },
        /* XK_F9                */      { 0xFFC6, KeyEvent.VK_F9 },
        /* XK_F10               */      { 0xFFC7, KeyEvent.VK_F10 },
        /* XK_F11               */      { 0xFFC8, KeyEvent.VK_F11 },
//      /* XK_L1                */      { 0xFFC8, KeyEvent.VK_L1 },
        /* XK_F12               */      { 0xFFC9, KeyEvent.VK_F12 },
//      /* XK_L2                */      { 0xFFC9, KeyEvent.VK_L2 },
        /* XK_F13               */      { 0xFFCA, KeyEvent.VK_F13 },
//      /* XK_L3                */      { 0xFFCA, KeyEvent.VK_L3 },
        /* XK_F14               */      { 0xFFCB, KeyEvent.VK_F14 },
//      /* XK_L4                */      { 0xFFCB, KeyEvent.VK_L4 },
        /* XK_F15               */      { 0xFFCC, KeyEvent.VK_F15 },
//      /* XK_L5                */      { 0xFFCC, KeyEvent.VK_L5 },
        /* XK_F16               */      { 0xFFCD, KeyEvent.VK_F16 },
//      /* XK_L6                */      { 0xFFCD, KeyEvent.VK_L6 },
        /* XK_F17               */      { 0xFFCE, KeyEvent.VK_F17 },
//      /* XK_L7                */      { 0xFFCE, KeyEvent.VK_L7 },
        /* XK_F18               */      { 0xFFCF, KeyEvent.VK_F18 },
//      /* XK_L8                */      { 0xFFCF, KeyEvent.VK_L8 },
        /* XK_F19               */      { 0xFFD0, KeyEvent.VK_F19 },
//      /* XK_L9                */      { 0xFFD0, KeyEvent.VK_L9 },
        /* XK_F20               */      { 0xFFD1, KeyEvent.VK_F20 },
//      /* XK_L10               */      { 0xFFD1, KeyEvent.VK_L10 },
        /* XK_F21               */      { 0xFFD2, KeyEvent.VK_F21 },
//      /* XK_R1                */      { 0xFFD2, KeyEvent.VK_R1 },
        /* XK_F22               */      { 0xFFD3, KeyEvent.VK_F22 },
//      /* XK_R2                */      { 0xFFD3, KeyEvent.VK_R2 },
        /* XK_F23               */      { 0xFFD4, KeyEvent.VK_F23 },
//      /* XK_R3                */      { 0xFFD4, KeyEvent.VK_R3 },
        /* XK_F24               */      { 0xFFD5, KeyEvent.VK_F24 },
//      /* XK_R4                */      { 0xFFD5, KeyEvent.VK_R4 },
//      /* XK_F25               */      { 0xFFD6, KeyEvent.VK_F25 },
//      /* XK_R5                */      { 0xFFD6, KeyEvent.VK_R5 },
//      /* XK_F26               */      { 0xFFD7, KeyEvent.VK_F26 },
//      /* XK_R6                */      { 0xFFD7, KeyEvent.VK_R6 },
//      /* XK_F27               */      { 0xFFD8, KeyEvent.VK_F27 },
//      /* XK_R7                */      { 0xFFD8, KeyEvent.VK_R7 },
//      /* XK_F28               */      { 0xFFD9, KeyEvent.VK_F28 },
//      /* XK_R8                */      { 0xFFD9, KeyEvent.VK_R8 },
//      /* XK_F29               */      { 0xFFDA, KeyEvent.VK_F29 },
//      /* XK_R9                */      { 0xFFDA, KeyEvent.VK_R9 },
//      /* XK_F30               */      { 0xFFDB, KeyEvent.VK_F30 },
//      /* XK_R10               */      { 0xFFDB, KeyEvent.VK_R10 },
//      /* XK_F31               */      { 0xFFDC, KeyEvent.VK_F31 },
//      /* XK_R11               */      { 0xFFDC, KeyEvent.VK_R11 },
//      /* XK_F32               */      { 0xFFDD, KeyEvent.VK_F32 },
//      /* XK_R12               */      { 0xFFDD, KeyEvent.VK_R12 },
//      /* XK_F33               */      { 0xFFDE, KeyEvent.VK_F33 },
//      /* XK_R13               */      { 0xFFDE, KeyEvent.VK_R13 },
//      /* XK_F34               */      { 0xFFDF, KeyEvent.VK_F34 },
//      /* XK_R14               */      { 0xFFDF, KeyEvent.VK_R14 },
//      /* XK_F35               */      { 0xFFE0, KeyEvent.VK_F35 },
//      /* XK_R15               */      { 0xFFE0, KeyEvent.VK_R15 },
        /* XK_Shift_L           */      { 0xFFE1, KeyEvent.VK_SHIFT },
//      /* XK_Shift_R           */      { 0xFFE2, KeyEvent.VK_SHIFT_R },
        /* XK_Control_L         */      { 0xFFE3, KeyEvent.VK_CONTROL },
//      /* XK_Control_R         */      { 0xFFE4, KeyEvent.VK_CONTROL_R },
        /* XK_Caps_Lock         */      { 0xFFE5, KeyEvent.VK_CAPS_LOCK },
//      /* XK_Shift_Lock        */      { 0xFFE6, KeyEvent.VK_SHIFT_LOCK },
        /* XK_Meta_L            */      { 0xFFE7, KeyEvent.VK_META },
//      /* XK_Meta_R            */      { 0xFFE8, KeyEvent.VK_META_R },
        /* XK_Alt_L             */      { 0xFFE9, KeyEvent.VK_ALT },
//      /* XK_Alt_R             */      { 0xFFEA, KeyEvent.VK_ALT_R },
//      /* XK_Super_L           */      { 0xFFEB, KeyEvent.VK_SUPER_L },
//      /* XK_Super_R           */      { 0xFFEC, KeyEvent.VK_SUPER_R },
//      /* XK_Hyper_L           */      { 0xFFED, KeyEvent.VK_HYPER_L },
//      /* XK_Hyper_R           */      { 0xFFEE, KeyEvent.VK_HYPER_R },
        /* XK_a                 */      { 0x0061, KeyEvent.VK_A },
        /* XK_b                 */      { 0x0062, KeyEvent.VK_B },
        /* XK_c                 */      { 0x0063, KeyEvent.VK_C },
        /* XK_d                 */      { 0x0064, KeyEvent.VK_D },
        /* XK_e                 */      { 0x0065, KeyEvent.VK_E },
        /* XK_f                 */      { 0x0066, KeyEvent.VK_F },
        /* XK_g                 */      { 0x0067, KeyEvent.VK_G },
        /* XK_h                 */      { 0x0068, KeyEvent.VK_H },
        /* XK_i                 */      { 0x0069, KeyEvent.VK_I },
        /* XK_j                 */      { 0x006a, KeyEvent.VK_J },
        /* XK_k                 */      { 0x006b, KeyEvent.VK_K },
        /* XK_l                 */      { 0x006c, KeyEvent.VK_L },
        /* XK_m                 */      { 0x006d, KeyEvent.VK_M },
        /* XK_n                 */      { 0x006e, KeyEvent.VK_N },
        /* XK_o                 */      { 0x006f, KeyEvent.VK_O },
        /* XK_p                 */      { 0x0070, KeyEvent.VK_P },
        /* XK_q                 */      { 0x0071, KeyEvent.VK_Q },
        /* XK_r                 */      { 0x0072, KeyEvent.VK_R },
        /* XK_s                 */      { 0x0073, KeyEvent.VK_S },
        /* XK_t                 */      { 0x0074, KeyEvent.VK_T },
        /* XK_u                 */      { 0x0075, KeyEvent.VK_U },
        /* XK_v                 */      { 0x0076, KeyEvent.VK_V },
        /* XK_w                 */      { 0x0077, KeyEvent.VK_W },
        /* XK_x                 */      { 0x0078, KeyEvent.VK_X },
        /* XK_y                 */      { 0x0079, KeyEvent.VK_Y },
        /* XK_z                 */      { 0x007a, KeyEvent.VK_Z },
    };

    /**
     * Gets an X11 keysym that matches the Java keycode.  If no
     * matches are found, returns a -1.
     *
     * @param keycode the Java keycode to convert
     * @return a matching X11 keysym or a -1 if no match was found.
     */
    public static int getKeysym(int keycode) {
        for (int i = 0; i < (map.length - 1); i++) {
            if (map[i][1] == keycode) {
                return map[i][0];
            }
        }
        return -1;
    }
}
