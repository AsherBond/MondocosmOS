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

import java.util.Map;
import java.util.TreeMap;
import org.jdesktop.wonderland.common.StableAPI;

/*
 * This code is a Java translation of a C source code in the public domain.
 * This file was created from the following X11 resource:
 *  
 *       http://www.cl.cam.ac.uk/~mgk25/ucs/keysym2ucs.c
 *   
 *   It came with the following header:
 *  
 *   This module converts keysym values into the corresponding ISO 10646
 *   (UCS, Unicode) values.
 *  
 *   The array keysymtab[] contains pairs of X11 keysym values for graphical
 *   characters and the corresponding Unicode value. The function
 *   keysym2ucs() maps a keysym onto a Unicode value using a binary search,
 *   therefore keysymtab[] must remain SORTED by keysym value.
 *  
 *   The keysym -> UTF-8 conversion will hopefully one day be provided
 *   by Xlib via XmbLookupString() and should ideally not have to be
 *   done in X applications. But we are not there yet.
 *  
 *   We allow to represent any UCS character in the range U-00000000 to
 *   U-00FFFFFF by a keysym value in the range 0x01000000 to 0x01ffffff.
 *   This admittedly does not cover the entire 31-bit space of UCS, but
 *   it does cover all of the characters up to U-10FFFF, which can be
 *   represented by UTF-16, and more, and it is very unlikely that higher
 *   UCS codes will ever be assigned by ISO. So to get Unicode character
 *   U+ABCD you can directly use keysym 0x0100abcd.
 *  
 *   NOTE: The comments in the table below contain the actual character
 *   encoded in UTF-8, so for viewing and editing best use an editor in
 *   UTF-8 mode.
 *  
 *   Author: Markus G. Kuhn <http://www.cl.cam.ac.uk/~mgk25/>,
 *           University of Cambridge, April 2001
 *  
 *   Special thanks to Richard Verhoeven <river@win.tue.nl> for preparing
 *   an initial draft of the mapping table.
 *  
 *   This software is in the public domain. Share and enjoy!
 *  
 *   AUTOMATICALLY GENERATED FILE, DO NOT EDIT !!! (unicode/convmap.pl)
 */

/**
 * Converts Unicode characters to X11 keysyms.
 */
@StableAPI
class UnicodeToKeysym {

    /* The format of each line is as follows: {keysym, unicode}
     *
     * [[[WDW - FIXME: For quicker searching, this table should be
     * sorted in order of unicode.]]]
     */
    private final static short[][] map = {
        { 0x01a1, 0x0104 }, /* ? LATIN CAPITAL LETTER A WITH OGONEK */
        { 0x01a2, 0x02d8 }, /* ? BREVE */
        { 0x01a3, 0x0141 }, /* ? LATIN CAPITAL LETTER L WITH STROKE */
        { 0x01a5, 0x013d }, /* ? LATIN CAPITAL LETTER L WITH CARON */
        { 0x01a6, 0x015a }, /* ? LATIN CAPITAL LETTER S WITH ACUTE */
        { 0x01a9, 0x0160 }, /* ? LATIN CAPITAL LETTER S WITH CARON */
        { 0x01aa, 0x015e }, /* ? LATIN CAPITAL LETTER S WITH CEDILLA */
        { 0x01ab, 0x0164 }, /* ? LATIN CAPITAL LETTER T WITH CARON */
        { 0x01ac, 0x0179 }, /* ? LATIN CAPITAL LETTER Z WITH ACUTE */
        { 0x01ae, 0x017d }, /* ? LATIN CAPITAL LETTER Z WITH CARON */
        { 0x01af, 0x017b }, /* ? LATIN CAPITAL LETTER Z WITH DOT ABOVE */
        { 0x01b1, 0x0105 }, /* ? LATIN SMALL LETTER A WITH OGONEK */
        { 0x01b2, 0x02db }, /* ? OGONEK */
        { 0x01b3, 0x0142 }, /* ? LATIN SMALL LETTER L WITH STROKE */
        { 0x01b5, 0x013e }, /* ? LATIN SMALL LETTER L WITH CARON */
        { 0x01b6, 0x015b }, /* ? LATIN SMALL LETTER S WITH ACUTE */
        { 0x01b7, 0x02c7 }, /* ? CARON */
        { 0x01b9, 0x0161 }, /* ? LATIN SMALL LETTER S WITH CARON */
        { 0x01ba, 0x015f }, /* ? LATIN SMALL LETTER S WITH CEDILLA */
        { 0x01bb, 0x0165 }, /* ? LATIN SMALL LETTER T WITH CARON */
        { 0x01bc, 0x017a }, /* ? LATIN SMALL LETTER Z WITH ACUTE */
        { 0x01bd, 0x02dd }, /* ? DOUBLE ACUTE ACCENT */
        { 0x01be, 0x017e }, /* ? LATIN SMALL LETTER Z WITH CARON */
        { 0x01bf, 0x017c }, /* ? LATIN SMALL LETTER Z WITH DOT ABOVE */
        { 0x01c0, 0x0154 }, /* ? LATIN CAPITAL LETTER R WITH ACUTE */
        { 0x01c3, 0x0102 }, /* ? LATIN CAPITAL LETTER A WITH BREVE */
        { 0x01c5, 0x0139 }, /* ? LATIN CAPITAL LETTER L WITH ACUTE */
        { 0x01c6, 0x0106 }, /* ? LATIN CAPITAL LETTER C WITH ACUTE */
        { 0x01c8, 0x010c }, /* ? LATIN CAPITAL LETTER C WITH CARON */
        { 0x01ca, 0x0118 }, /* ? LATIN CAPITAL LETTER E WITH OGONEK */
        { 0x01cc, 0x011a }, /* ? LATIN CAPITAL LETTER E WITH CARON */
        { 0x01cf, 0x010e }, /* ? LATIN CAPITAL LETTER D WITH CARON */
        { 0x01d0, 0x0110 }, /* ? LATIN CAPITAL LETTER D WITH STROKE */
        { 0x01d1, 0x0143 }, /* ? LATIN CAPITAL LETTER N WITH ACUTE */
        { 0x01d2, 0x0147 }, /* ? LATIN CAPITAL LETTER N WITH CARON */
        { 0x01d5, 0x0150 }, /* ? LATIN CAPITAL LETTER O WITH DOUBLE ACUTE */
        { 0x01d8, 0x0158 }, /* ? LATIN CAPITAL LETTER R WITH CARON */
        { 0x01d9, 0x016e }, /* ? LATIN CAPITAL LETTER U WITH RING ABOVE */
        { 0x01db, 0x0170 }, /* ? LATIN CAPITAL LETTER U WITH DOUBLE ACUTE */
        { 0x01de, 0x0162 }, /* ? LATIN CAPITAL LETTER T WITH CEDILLA */
        { 0x01e0, 0x0155 }, /* ? LATIN SMALL LETTER R WITH ACUTE */
        { 0x01e3, 0x0103 }, /* ? LATIN SMALL LETTER A WITH BREVE */
        { 0x01e5, 0x013a }, /* ? LATIN SMALL LETTER L WITH ACUTE */
        { 0x01e6, 0x0107 }, /* ? LATIN SMALL LETTER C WITH ACUTE */
        { 0x01e8, 0x010d }, /* ? LATIN SMALL LETTER C WITH CARON */
        { 0x01ea, 0x0119 }, /* ? LATIN SMALL LETTER E WITH OGONEK */
        { 0x01ec, 0x011b }, /* ? LATIN SMALL LETTER E WITH CARON */
        { 0x01ef, 0x010f }, /* ? LATIN SMALL LETTER D WITH CARON */
        { 0x01f0, 0x0111 }, /* ? LATIN SMALL LETTER D WITH STROKE */
        { 0x01f1, 0x0144 }, /* ? LATIN SMALL LETTER N WITH ACUTE */
        { 0x01f2, 0x0148 }, /* ? LATIN SMALL LETTER N WITH CARON */
        { 0x01f5, 0x0151 }, /* ? LATIN SMALL LETTER O WITH DOUBLE ACUTE */
        { 0x01f8, 0x0159 }, /* ? LATIN SMALL LETTER R WITH CARON */
        { 0x01f9, 0x016f }, /* ? LATIN SMALL LETTER U WITH RING ABOVE */
        { 0x01fb, 0x0171 }, /* ? LATIN SMALL LETTER U WITH DOUBLE ACUTE */
        { 0x01fe, 0x0163 }, /* ? LATIN SMALL LETTER T WITH CEDILLA */
        { 0x01ff, 0x02d9 }, /* ? DOT ABOVE */
        { 0x02a1, 0x0126 }, /* ? LATIN CAPITAL LETTER H WITH STROKE */
        { 0x02a6, 0x0124 }, /* ? LATIN CAPITAL LETTER H WITH CIRCUMFLEX */
        { 0x02a9, 0x0130 }, /* ? LATIN CAPITAL LETTER I WITH DOT ABOVE */
        { 0x02ab, 0x011e }, /* ? LATIN CAPITAL LETTER G WITH BREVE */
        { 0x02ac, 0x0134 }, /* ? LATIN CAPITAL LETTER J WITH CIRCUMFLEX */
        { 0x02b1, 0x0127 }, /* ? LATIN SMALL LETTER H WITH STROKE */
        { 0x02b6, 0x0125 }, /* ? LATIN SMALL LETTER H WITH CIRCUMFLEX */
        { 0x02b9, 0x0131 }, /* ? LATIN SMALL LETTER DOTLESS I */
        { 0x02bb, 0x011f }, /* ? LATIN SMALL LETTER G WITH BREVE */
        { 0x02bc, 0x0135 }, /* ? LATIN SMALL LETTER J WITH CIRCUMFLEX */
        { 0x02c5, 0x010a }, /* ? LATIN CAPITAL LETTER C WITH DOT ABOVE */
        { 0x02c6, 0x0108 }, /* ? LATIN CAPITAL LETTER C WITH CIRCUMFLEX */
        { 0x02d5, 0x0120 }, /* ? LATIN CAPITAL LETTER G WITH DOT ABOVE */
        { 0x02d8, 0x011c }, /* ? LATIN CAPITAL LETTER G WITH CIRCUMFLEX */
        { 0x02dd, 0x016c }, /* ? LATIN CAPITAL LETTER U WITH BREVE */
        { 0x02de, 0x015c }, /* ? LATIN CAPITAL LETTER S WITH CIRCUMFLEX */
        { 0x02e5, 0x010b }, /* ? LATIN SMALL LETTER C WITH DOT ABOVE */
        { 0x02e6, 0x0109 }, /* ? LATIN SMALL LETTER C WITH CIRCUMFLEX */
        { 0x02f5, 0x0121 }, /* ? LATIN SMALL LETTER G WITH DOT ABOVE */
        { 0x02f8, 0x011d }, /* ? LATIN SMALL LETTER G WITH CIRCUMFLEX */
        { 0x02fd, 0x016d }, /* ? LATIN SMALL LETTER U WITH BREVE */
        { 0x02fe, 0x015d }, /* ? LATIN SMALL LETTER S WITH CIRCUMFLEX */
        { 0x03a2, 0x0138 }, /* ? LATIN SMALL LETTER KRA */
        { 0x03a3, 0x0156 }, /* ? LATIN CAPITAL LETTER R WITH CEDILLA */
        { 0x03a5, 0x0128 }, /* ? LATIN CAPITAL LETTER I WITH TILDE */
        { 0x03a6, 0x013b }, /* ? LATIN CAPITAL LETTER L WITH CEDILLA */
        { 0x03aa, 0x0112 }, /* ? LATIN CAPITAL LETTER E WITH MACRON */
        { 0x03ab, 0x0122 }, /* ? LATIN CAPITAL LETTER G WITH CEDILLA */
        { 0x03ac, 0x0166 }, /* ? LATIN CAPITAL LETTER T WITH STROKE */
        { 0x03b3, 0x0157 }, /* ? LATIN SMALL LETTER R WITH CEDILLA */
        { 0x03b5, 0x0129 }, /* ? LATIN SMALL LETTER I WITH TILDE */
        { 0x03b6, 0x013c }, /* ? LATIN SMALL LETTER L WITH CEDILLA */
        { 0x03ba, 0x0113 }, /* ? LATIN SMALL LETTER E WITH MACRON */
        { 0x03bb, 0x0123 }, /* ? LATIN SMALL LETTER G WITH CEDILLA */
        { 0x03bc, 0x0167 }, /* ? LATIN SMALL LETTER T WITH STROKE */
        { 0x03bd, 0x014a }, /* ? LATIN CAPITAL LETTER ENG */
        { 0x03bf, 0x014b }, /* ? LATIN SMALL LETTER ENG */
        { 0x03c0, 0x0100 }, /* ? LATIN CAPITAL LETTER A WITH MACRON */
        { 0x03c7, 0x012e }, /* ? LATIN CAPITAL LETTER I WITH OGONEK */
        { 0x03cc, 0x0116 }, /* ? LATIN CAPITAL LETTER E WITH DOT ABOVE */
        { 0x03cf, 0x012a }, /* ? LATIN CAPITAL LETTER I WITH MACRON */
        { 0x03d1, 0x0145 }, /* ? LATIN CAPITAL LETTER N WITH CEDILLA */
        { 0x03d2, 0x014c }, /* ? LATIN CAPITAL LETTER O WITH MACRON */
        { 0x03d3, 0x0136 }, /* ? LATIN CAPITAL LETTER K WITH CEDILLA */
        { 0x03d9, 0x0172 }, /* ? LATIN CAPITAL LETTER U WITH OGONEK */
        { 0x03dd, 0x0168 }, /* ? LATIN CAPITAL LETTER U WITH TILDE */
        { 0x03de, 0x016a }, /* ? LATIN CAPITAL LETTER U WITH MACRON */
        { 0x03e0, 0x0101 }, /* ? LATIN SMALL LETTER A WITH MACRON */
        { 0x03e7, 0x012f }, /* ? LATIN SMALL LETTER I WITH OGONEK */
        { 0x03ec, 0x0117 }, /* ? LATIN SMALL LETTER E WITH DOT ABOVE */
        { 0x03ef, 0x012b }, /* ? LATIN SMALL LETTER I WITH MACRON */
        { 0x03f1, 0x0146 }, /* ? LATIN SMALL LETTER N WITH CEDILLA */
        { 0x03f2, 0x014d }, /* ? LATIN SMALL LETTER O WITH MACRON */
        { 0x03f3, 0x0137 }, /* ? LATIN SMALL LETTER K WITH CEDILLA */
        { 0x03f9, 0x0173 }, /* ? LATIN SMALL LETTER U WITH OGONEK */
        { 0x03fd, 0x0169 }, /* ? LATIN SMALL LETTER U WITH TILDE */
        { 0x03fe, 0x016b }, /* ? LATIN SMALL LETTER U WITH MACRON */
        { 0x047e, 0x203e }, /* ? OVERLINE */
        { 0x04a1, 0x3002 }, /* ? IDEOGRAPHIC FULL STOP */
        { 0x04a2, 0x300c }, /* ? LEFT CORNER BRACKET */
        { 0x04a3, 0x300d }, /* ? RIGHT CORNER BRACKET */
        { 0x04a4, 0x3001 }, /* ? IDEOGRAPHIC COMMA */
        { 0x04a5, 0x30fb }, /* ? KATAKANA MIDDLE DOT */
        { 0x04a6, 0x30f2 }, /* ? KATAKANA LETTER WO */
        { 0x04a7, 0x30a1 }, /* ? KATAKANA LETTER SMALL A */
        { 0x04a8, 0x30a3 }, /* ? KATAKANA LETTER SMALL I */
        { 0x04a9, 0x30a5 }, /* ? KATAKANA LETTER SMALL U */
        { 0x04aa, 0x30a7 }, /* ? KATAKANA LETTER SMALL E */
        { 0x04ab, 0x30a9 }, /* ? KATAKANA LETTER SMALL O */
        { 0x04ac, 0x30e3 }, /* ? KATAKANA LETTER SMALL YA */
        { 0x04ad, 0x30e5 }, /* ? KATAKANA LETTER SMALL YU */
        { 0x04ae, 0x30e7 }, /* ? KATAKANA LETTER SMALL YO */
        { 0x04af, 0x30c3 }, /* ? KATAKANA LETTER SMALL TU */
        { 0x04b0, 0x30fc }, /* ? KATAKANA-HIRAGANA PROLONGED SOUND MARK */
        { 0x04b1, 0x30a2 }, /* ? KATAKANA LETTER A */
        { 0x04b2, 0x30a4 }, /* ? KATAKANA LETTER I */
        { 0x04b3, 0x30a6 }, /* ? KATAKANA LETTER U */
        { 0x04b4, 0x30a8 }, /* ? KATAKANA LETTER E */
        { 0x04b5, 0x30aa }, /* ? KATAKANA LETTER O */
        { 0x04b6, 0x30ab }, /* ? KATAKANA LETTER KA */
        { 0x04b7, 0x30ad }, /* ? KATAKANA LETTER KI */
        { 0x04b8, 0x30af }, /* ? KATAKANA LETTER KU */
        { 0x04b9, 0x30b1 }, /* ? KATAKANA LETTER KE */
        { 0x04ba, 0x30b3 }, /* ? KATAKANA LETTER KO */
        { 0x04bb, 0x30b5 }, /* ? KATAKANA LETTER SA */
        { 0x04bc, 0x30b7 }, /* ? KATAKANA LETTER SI */
        { 0x04bd, 0x30b9 }, /* ? KATAKANA LETTER SU */
        { 0x04be, 0x30bb }, /* ? KATAKANA LETTER SE */
        { 0x04bf, 0x30bd }, /* ? KATAKANA LETTER SO */
        { 0x04c0, 0x30bf }, /* ? KATAKANA LETTER TA */
        { 0x04c1, 0x30c1 }, /* ? KATAKANA LETTER TI */
        { 0x04c2, 0x30c4 }, /* ? KATAKANA LETTER TU */
        { 0x04c3, 0x30c6 }, /* ? KATAKANA LETTER TE */
        { 0x04c4, 0x30c8 }, /* ? KATAKANA LETTER TO */
        { 0x04c5, 0x30ca }, /* ? KATAKANA LETTER NA */
        { 0x04c6, 0x30cb }, /* ? KATAKANA LETTER NI */
        { 0x04c7, 0x30cc }, /* ? KATAKANA LETTER NU */
        { 0x04c8, 0x30cd }, /* ? KATAKANA LETTER NE */
        { 0x04c9, 0x30ce }, /* ? KATAKANA LETTER NO */
        { 0x04ca, 0x30cf }, /* ? KATAKANA LETTER HA */
        { 0x04cb, 0x30d2 }, /* ? KATAKANA LETTER HI */
        { 0x04cc, 0x30d5 }, /* ? KATAKANA LETTER HU */
        { 0x04cd, 0x30d8 }, /* ? KATAKANA LETTER HE */
        { 0x04ce, 0x30db }, /* ? KATAKANA LETTER HO */
        { 0x04cf, 0x30de }, /* ? KATAKANA LETTER MA */
        { 0x04d0, 0x30df }, /* ? KATAKANA LETTER MI */
        { 0x04d1, 0x30e0 }, /* ? KATAKANA LETTER MU */
        { 0x04d2, 0x30e1 }, /* ? KATAKANA LETTER ME */
        { 0x04d3, 0x30e2 }, /* ? KATAKANA LETTER MO */
        { 0x04d4, 0x30e4 }, /* ? KATAKANA LETTER YA */
        { 0x04d5, 0x30e6 }, /* ? KATAKANA LETTER YU */
        { 0x04d6, 0x30e8 }, /* ? KATAKANA LETTER YO */
        { 0x04d7, 0x30e9 }, /* ? KATAKANA LETTER RA */
        { 0x04d8, 0x30ea }, /* ? KATAKANA LETTER RI */
        { 0x04d9, 0x30eb }, /* ? KATAKANA LETTER RU */
        { 0x04da, 0x30ec }, /* ? KATAKANA LETTER RE */
        { 0x04db, 0x30ed }, /* ? KATAKANA LETTER RO */
        { 0x04dc, 0x30ef }, /* ? KATAKANA LETTER WA */
        { 0x04dd, 0x30f3 }, /* ? KATAKANA LETTER N */
        { 0x04de, 0x309b }, /* ? KATAKANA-HIRAGANA VOICED SOUND MARK */
        { 0x04df, 0x309c }, /* ? KATAKANA-HIRAGANA SEMI-VOICED SOUND MARK */
        { 0x05ac, 0x060c }, /* ? ARABIC COMMA */
        { 0x05bb, 0x061b }, /* ? ARABIC SEMICOLON */
        { 0x05bf, 0x061f }, /* ? ARABIC QUESTION MARK */
        { 0x05c1, 0x0621 }, /* ? ARABIC LETTER HAMZA */
        { 0x05c2, 0x0622 }, /* ? ARABIC LETTER ALEF WITH MADDA ABOVE */
        { 0x05c3, 0x0623 }, /* ? ARABIC LETTER ALEF WITH HAMZA ABOVE */
        { 0x05c4, 0x0624 }, /* ? ARABIC LETTER WAW WITH HAMZA ABOVE */
        { 0x05c5, 0x0625 }, /* ? ARABIC LETTER ALEF WITH HAMZA BELOW */
        { 0x05c6, 0x0626 }, /* ? ARABIC LETTER YEH WITH HAMZA ABOVE */
        { 0x05c7, 0x0627 }, /* ? ARABIC LETTER ALEF */
        { 0x05c8, 0x0628 }, /* ? ARABIC LETTER BEH */
        { 0x05c9, 0x0629 }, /* ? ARABIC LETTER TEH MARBUTA */
        { 0x05ca, 0x062a }, /* ? ARABIC LETTER TEH */
        { 0x05cb, 0x062b }, /* ? ARABIC LETTER THEH */
        { 0x05cc, 0x062c }, /* ? ARABIC LETTER JEEM */
        { 0x05cd, 0x062d }, /* ? ARABIC LETTER HAH */
        { 0x05ce, 0x062e }, /* ? ARABIC LETTER KHAH */
        { 0x05cf, 0x062f }, /* ? ARABIC LETTER DAL */
        { 0x05d0, 0x0630 }, /* ? ARABIC LETTER THAL */
        { 0x05d1, 0x0631 }, /* ? ARABIC LETTER REH */
        { 0x05d2, 0x0632 }, /* ? ARABIC LETTER ZAIN */
        { 0x05d3, 0x0633 }, /* ? ARABIC LETTER SEEN */
        { 0x05d4, 0x0634 }, /* ? ARABIC LETTER SHEEN */
        { 0x05d5, 0x0635 }, /* ? ARABIC LETTER SAD */
        { 0x05d6, 0x0636 }, /* ? ARABIC LETTER DAD */
        { 0x05d7, 0x0637 }, /* ? ARABIC LETTER TAH */
        { 0x05d8, 0x0638 }, /* ? ARABIC LETTER ZAH */
        { 0x05d9, 0x0639 }, /* ? ARABIC LETTER AIN */
        { 0x05da, 0x063a }, /* ? ARABIC LETTER GHAIN */
        { 0x05e0, 0x0640 }, /* ? ARABIC TATWEEL */
        { 0x05e1, 0x0641 }, /* ? ARABIC LETTER FEH */
        { 0x05e2, 0x0642 }, /* ? ARABIC LETTER QAF */
        { 0x05e3, 0x0643 }, /* ? ARABIC LETTER KAF */
        { 0x05e4, 0x0644 }, /* ? ARABIC LETTER LAM */
        { 0x05e5, 0x0645 }, /* ? ARABIC LETTER MEEM */
        { 0x05e6, 0x0646 }, /* ? ARABIC LETTER NOON */
        { 0x05e7, 0x0647 }, /* ? ARABIC LETTER HEH */
        { 0x05e8, 0x0648 }, /* ? ARABIC LETTER WAW */
        { 0x05e9, 0x0649 }, /* ? ARABIC LETTER ALEF MAKSURA */
        { 0x05ea, 0x064a }, /* ? ARABIC LETTER YEH */
        { 0x05eb, 0x064b }, /* ? ARABIC FATHATAN */
        { 0x05ec, 0x064c }, /* ? ARABIC DAMMATAN */
        { 0x05ed, 0x064d }, /* ? ARABIC KASRATAN */
        { 0x05ee, 0x064e }, /* ? ARABIC FATHA */
        { 0x05ef, 0x064f }, /* ? ARABIC DAMMA */
        { 0x05f0, 0x0650 }, /* ? ARABIC KASRA */
        { 0x05f1, 0x0651 }, /* ? ARABIC SHADDA */
        { 0x05f2, 0x0652 }, /* ? ARABIC SUKUN */
        { 0x06a1, 0x0452 }, /* ? CYRILLIC SMALL LETTER DJE */
        { 0x06a2, 0x0453 }, /* ? CYRILLIC SMALL LETTER GJE */
        { 0x06a3, 0x0451 }, /* ? CYRILLIC SMALL LETTER IO */
        { 0x06a4, 0x0454 }, /* ? CYRILLIC SMALL LETTER UKRAINIAN IE */
        { 0x06a5, 0x0455 }, /* ? CYRILLIC SMALL LETTER DZE */
        { 0x06a6, 0x0456 }, /* ? CYRILLIC SMALL LETTER BYELORUSSIAN-UKRAINIAN I */
        { 0x06a7, 0x0457 }, /* ? CYRILLIC SMALL LETTER YI */
        { 0x06a8, 0x0458 }, /* ? CYRILLIC SMALL LETTER JE */
        { 0x06a9, 0x0459 }, /* ? CYRILLIC SMALL LETTER LJE */
        { 0x06aa, 0x045a }, /* ? CYRILLIC SMALL LETTER NJE */
        { 0x06ab, 0x045b }, /* ? CYRILLIC SMALL LETTER TSHE */
        { 0x06ac, 0x045c }, /* ? CYRILLIC SMALL LETTER KJE */
        { 0x06ae, 0x045e }, /* ? CYRILLIC SMALL LETTER SHORT U */
        { 0x06af, 0x045f }, /* ? CYRILLIC SMALL LETTER DZHE */
        { 0x06b0, 0x2116 }, /* ? NUMERO SIGN */
        { 0x06b1, 0x0402 }, /* ? CYRILLIC CAPITAL LETTER DJE */
        { 0x06b2, 0x0403 }, /* ? CYRILLIC CAPITAL LETTER GJE */
        { 0x06b3, 0x0401 }, /* ? CYRILLIC CAPITAL LETTER IO */
        { 0x06b4, 0x0404 }, /* ? CYRILLIC CAPITAL LETTER UKRAINIAN IE */
        { 0x06b5, 0x0405 }, /* ? CYRILLIC CAPITAL LETTER DZE */
        { 0x06b6, 0x0406 }, /* ? CYRILLIC CAPITAL LETTER BYELORUSSIAN-UKRAINIAN I */
        { 0x06b7, 0x0407 }, /* ? CYRILLIC CAPITAL LETTER YI */
        { 0x06b8, 0x0408 }, /* ? CYRILLIC CAPITAL LETTER JE */
        { 0x06b9, 0x0409 }, /* ? CYRILLIC CAPITAL LETTER LJE */
        { 0x06ba, 0x040a }, /* ? CYRILLIC CAPITAL LETTER NJE */
        { 0x06bb, 0x040b }, /* ? CYRILLIC CAPITAL LETTER TSHE */
        { 0x06bc, 0x040c }, /* ? CYRILLIC CAPITAL LETTER KJE */
        { 0x06be, 0x040e }, /* ? CYRILLIC CAPITAL LETTER SHORT U */
        { 0x06bf, 0x040f }, /* ? CYRILLIC CAPITAL LETTER DZHE */
        { 0x06c0, 0x044e }, /* ? CYRILLIC SMALL LETTER YU */
        { 0x06c1, 0x0430 }, /* ? CYRILLIC SMALL LETTER A */
        { 0x06c2, 0x0431 }, /* ? CYRILLIC SMALL LETTER BE */
        { 0x06c3, 0x0446 }, /* ? CYRILLIC SMALL LETTER TSE */
        { 0x06c4, 0x0434 }, /* ? CYRILLIC SMALL LETTER DE */
        { 0x06c5, 0x0435 }, /* ? CYRILLIC SMALL LETTER IE */
        { 0x06c6, 0x0444 }, /* ? CYRILLIC SMALL LETTER EF */
        { 0x06c7, 0x0433 }, /* ? CYRILLIC SMALL LETTER GHE */
        { 0x06c8, 0x0445 }, /* ? CYRILLIC SMALL LETTER HA */
        { 0x06c9, 0x0438 }, /* ? CYRILLIC SMALL LETTER I */
        { 0x06ca, 0x0439 }, /* ? CYRILLIC SMALL LETTER SHORT I */
        { 0x06cb, 0x043a }, /* ? CYRILLIC SMALL LETTER KA */
        { 0x06cc, 0x043b }, /* ? CYRILLIC SMALL LETTER EL */
        { 0x06cd, 0x043c }, /* ? CYRILLIC SMALL LETTER EM */
        { 0x06ce, 0x043d }, /* ? CYRILLIC SMALL LETTER EN */
        { 0x06cf, 0x043e }, /* ? CYRILLIC SMALL LETTER O */
        { 0x06d0, 0x043f }, /* ? CYRILLIC SMALL LETTER PE */
        { 0x06d1, 0x044f }, /* ? CYRILLIC SMALL LETTER YA */
        { 0x06d2, 0x0440 }, /* ? CYRILLIC SMALL LETTER ER */
        { 0x06d3, 0x0441 }, /* ? CYRILLIC SMALL LETTER ES */
        { 0x06d4, 0x0442 }, /* ? CYRILLIC SMALL LETTER TE */
        { 0x06d5, 0x0443 }, /* ? CYRILLIC SMALL LETTER U */
        { 0x06d6, 0x0436 }, /* ? CYRILLIC SMALL LETTER ZHE */
        { 0x06d7, 0x0432 }, /* ? CYRILLIC SMALL LETTER VE */
        { 0x06d8, 0x044c }, /* ? CYRILLIC SMALL LETTER SOFT SIGN */
        { 0x06d9, 0x044b }, /* ? CYRILLIC SMALL LETTER YERU */
        { 0x06da, 0x0437 }, /* ? CYRILLIC SMALL LETTER ZE */
        { 0x06db, 0x0448 }, /* ? CYRILLIC SMALL LETTER SHA */
        { 0x06dc, 0x044d }, /* ? CYRILLIC SMALL LETTER E */
        { 0x06dd, 0x0449 }, /* ? CYRILLIC SMALL LETTER SHCHA */
        { 0x06de, 0x0447 }, /* ? CYRILLIC SMALL LETTER CHE */
        { 0x06df, 0x044a }, /* ? CYRILLIC SMALL LETTER HARD SIGN */
        { 0x06e0, 0x042e }, /* ? CYRILLIC CAPITAL LETTER YU */
        { 0x06e1, 0x0410 }, /* ? CYRILLIC CAPITAL LETTER A */
        { 0x06e2, 0x0411 }, /* ? CYRILLIC CAPITAL LETTER BE */
        { 0x06e3, 0x0426 }, /* ? CYRILLIC CAPITAL LETTER TSE */
        { 0x06e4, 0x0414 }, /* ? CYRILLIC CAPITAL LETTER DE */
        { 0x06e5, 0x0415 }, /* ? CYRILLIC CAPITAL LETTER IE */
        { 0x06e6, 0x0424 }, /* ? CYRILLIC CAPITAL LETTER EF */
        { 0x06e7, 0x0413 }, /* ? CYRILLIC CAPITAL LETTER GHE */
        { 0x06e8, 0x0425 }, /* ? CYRILLIC CAPITAL LETTER HA */
        { 0x06e9, 0x0418 }, /* ? CYRILLIC CAPITAL LETTER I */
        { 0x06ea, 0x0419 }, /* ? CYRILLIC CAPITAL LETTER SHORT I */
        { 0x06eb, 0x041a }, /* ? CYRILLIC CAPITAL LETTER KA */
        { 0x06ec, 0x041b }, /* ? CYRILLIC CAPITAL LETTER EL */
        { 0x06ed, 0x041c }, /* ? CYRILLIC CAPITAL LETTER EM */
        { 0x06ee, 0x041d }, /* ? CYRILLIC CAPITAL LETTER EN */
        { 0x06ef, 0x041e }, /* ? CYRILLIC CAPITAL LETTER O */
        { 0x06f0, 0x041f }, /* ? CYRILLIC CAPITAL LETTER PE */
        { 0x06f1, 0x042f }, /* ? CYRILLIC CAPITAL LETTER YA */
        { 0x06f2, 0x0420 }, /* ? CYRILLIC CAPITAL LETTER ER */
        { 0x06f3, 0x0421 }, /* ? CYRILLIC CAPITAL LETTER ES */
        { 0x06f4, 0x0422 }, /* ? CYRILLIC CAPITAL LETTER TE */
        { 0x06f5, 0x0423 }, /* ? CYRILLIC CAPITAL LETTER U */
        { 0x06f6, 0x0416 }, /* ? CYRILLIC CAPITAL LETTER ZHE */
        { 0x06f7, 0x0412 }, /* ? CYRILLIC CAPITAL LETTER VE */
        { 0x06f8, 0x042c }, /* ? CYRILLIC CAPITAL LETTER SOFT SIGN */
        { 0x06f9, 0x042b }, /* ? CYRILLIC CAPITAL LETTER YERU */
        { 0x06fa, 0x0417 }, /* ? CYRILLIC CAPITAL LETTER ZE */
        { 0x06fb, 0x0428 }, /* ? CYRILLIC CAPITAL LETTER SHA */
        { 0x06fc, 0x042d }, /* ? CYRILLIC CAPITAL LETTER E */
        { 0x06fd, 0x0429 }, /* ? CYRILLIC CAPITAL LETTER SHCHA */
        { 0x06fe, 0x0427 }, /* ? CYRILLIC CAPITAL LETTER CHE */
        { 0x06ff, 0x042a }, /* ? CYRILLIC CAPITAL LETTER HARD SIGN */
        { 0x07a1, 0x0386 }, /* ? GREEK CAPITAL LETTER ALPHA WITH TONOS */
        { 0x07a2, 0x0388 }, /* ? GREEK CAPITAL LETTER EPSILON WITH TONOS */
        { 0x07a3, 0x0389 }, /* ? GREEK CAPITAL LETTER ETA WITH TONOS */
        { 0x07a4, 0x038a }, /* ? GREEK CAPITAL LETTER IOTA WITH TONOS */
        { 0x07a5, 0x03aa }, /* ? GREEK CAPITAL LETTER IOTA WITH DIALYTIKA */
        { 0x07a7, 0x038c }, /* ? GREEK CAPITAL LETTER OMICRON WITH TONOS */
        { 0x07a8, 0x038e }, /* ? GREEK CAPITAL LETTER UPSILON WITH TONOS */
        { 0x07a9, 0x03ab }, /* ? GREEK CAPITAL LETTER UPSILON WITH DIALYTIKA */
        { 0x07ab, 0x038f }, /* ? GREEK CAPITAL LETTER OMEGA WITH TONOS */
        { 0x07ae, 0x0385 }, /* ? GREEK DIALYTIKA TONOS */
        { 0x07af, 0x2015 }, /* ? HORIZONTAL BAR */
        { 0x07b1, 0x03ac }, /* ? GREEK SMALL LETTER ALPHA WITH TONOS */
        { 0x07b2, 0x03ad }, /* ? GREEK SMALL LETTER EPSILON WITH TONOS */
        { 0x07b3, 0x03ae }, /* ? GREEK SMALL LETTER ETA WITH TONOS */
        { 0x07b4, 0x03af }, /* ? GREEK SMALL LETTER IOTA WITH TONOS */
        { 0x07b5, 0x03ca }, /* ? GREEK SMALL LETTER IOTA WITH DIALYTIKA */
        { 0x07b6, 0x0390 }, /* ? GREEK SMALL LETTER IOTA WITH DIALYTIKA AND TONOS */
        { 0x07b7, 0x03cc }, /* ? GREEK SMALL LETTER OMICRON WITH TONOS */
        { 0x07b8, 0x03cd }, /* ? GREEK SMALL LETTER UPSILON WITH TONOS */
        { 0x07b9, 0x03cb }, /* ? GREEK SMALL LETTER UPSILON WITH DIALYTIKA */
        { 0x07ba, 0x03b0 }, /* ? GREEK SMALL LETTER UPSILON WITH DIALYTIKA AND TONOS */
        { 0x07bb, 0x03ce }, /* ? GREEK SMALL LETTER OMEGA WITH TONOS */
        { 0x07c1, 0x0391 }, /* ? GREEK CAPITAL LETTER ALPHA */
        { 0x07c2, 0x0392 }, /* ? GREEK CAPITAL LETTER BETA */
        { 0x07c3, 0x0393 }, /* ? GREEK CAPITAL LETTER GAMMA */
        { 0x07c4, 0x0394 }, /* ? GREEK CAPITAL LETTER DELTA */
        { 0x07c5, 0x0395 }, /* ? GREEK CAPITAL LETTER EPSILON */
        { 0x07c6, 0x0396 }, /* ? GREEK CAPITAL LETTER ZETA */
        { 0x07c7, 0x0397 }, /* ? GREEK CAPITAL LETTER ETA */
        { 0x07c8, 0x0398 }, /* ? GREEK CAPITAL LETTER THETA */
        { 0x07c9, 0x0399 }, /* ? GREEK CAPITAL LETTER IOTA */
        { 0x07ca, 0x039a }, /* ? GREEK CAPITAL LETTER KAPPA */
        { 0x07cb, 0x039b }, /* ? GREEK CAPITAL LETTER LAMDA */
        { 0x07cc, 0x039c }, /* ? GREEK CAPITAL LETTER MU */
        { 0x07cd, 0x039d }, /* ? GREEK CAPITAL LETTER NU */
        { 0x07ce, 0x039e }, /* ? GREEK CAPITAL LETTER XI */
        { 0x07cf, 0x039f }, /* ? GREEK CAPITAL LETTER OMICRON */
        { 0x07d0, 0x03a0 }, /* ? GREEK CAPITAL LETTER PI */
        { 0x07d1, 0x03a1 }, /* ? GREEK CAPITAL LETTER RHO */
        { 0x07d2, 0x03a3 }, /* ? GREEK CAPITAL LETTER SIGMA */
        { 0x07d4, 0x03a4 }, /* ? GREEK CAPITAL LETTER TAU */
        { 0x07d5, 0x03a5 }, /* ? GREEK CAPITAL LETTER UPSILON */
        { 0x07d6, 0x03a6 }, /* ? GREEK CAPITAL LETTER PHI */
        { 0x07d7, 0x03a7 }, /* ? GREEK CAPITAL LETTER CHI */
        { 0x07d8, 0x03a8 }, /* ? GREEK CAPITAL LETTER PSI */
        { 0x07d9, 0x03a9 }, /* ? GREEK CAPITAL LETTER OMEGA */
        { 0x07e1, 0x03b1 }, /* ? GREEK SMALL LETTER ALPHA */
        { 0x07e2, 0x03b2 }, /* ? GREEK SMALL LETTER BETA */
        { 0x07e3, 0x03b3 }, /* ? GREEK SMALL LETTER GAMMA */
        { 0x07e4, 0x03b4 }, /* ? GREEK SMALL LETTER DELTA */
        { 0x07e5, 0x03b5 }, /* ? GREEK SMALL LETTER EPSILON */
        { 0x07e6, 0x03b6 }, /* ? GREEK SMALL LETTER ZETA */
        { 0x07e7, 0x03b7 }, /* ? GREEK SMALL LETTER ETA */
        { 0x07e8, 0x03b8 }, /* ? GREEK SMALL LETTER THETA */
        { 0x07e9, 0x03b9 }, /* ? GREEK SMALL LETTER IOTA */
        { 0x07ea, 0x03ba }, /* ? GREEK SMALL LETTER KAPPA */
        { 0x07eb, 0x03bb }, /* ? GREEK SMALL LETTER LAMDA */
        { 0x07ec, 0x03bc }, /* ? GREEK SMALL LETTER MU */
        { 0x07ed, 0x03bd }, /* ? GREEK SMALL LETTER NU */
        { 0x07ee, 0x03be }, /* ? GREEK SMALL LETTER XI */
        { 0x07ef, 0x03bf }, /* ? GREEK SMALL LETTER OMICRON */
        { 0x07f0, 0x03c0 }, /* ? GREEK SMALL LETTER PI */
        { 0x07f1, 0x03c1 }, /* ? GREEK SMALL LETTER RHO */
        { 0x07f2, 0x03c3 }, /* ? GREEK SMALL LETTER SIGMA */
        { 0x07f3, 0x03c2 }, /* ? GREEK SMALL LETTER FINAL SIGMA */
        { 0x07f4, 0x03c4 }, /* ? GREEK SMALL LETTER TAU */
        { 0x07f5, 0x03c5 }, /* ? GREEK SMALL LETTER UPSILON */
        { 0x07f6, 0x03c6 }, /* ? GREEK SMALL LETTER PHI */
        { 0x07f7, 0x03c7 }, /* ? GREEK SMALL LETTER CHI */
        { 0x07f8, 0x03c8 }, /* ? GREEK SMALL LETTER PSI */
        { 0x07f9, 0x03c9 }, /* ? GREEK SMALL LETTER OMEGA */
        { 0x08a1, 0x23b7 }, /* ? ??? */
        { 0x08a2, 0x250c }, /* ? BOX DRAWINGS LIGHT DOWN AND RIGHT */
        { 0x08a3, 0x2500 }, /* ? BOX DRAWINGS LIGHT HORIZONTAL */
        { 0x08a4, 0x2320 }, /* ? TOP HALF INTEGRAL */
        { 0x08a5, 0x2321 }, /* ? BOTTOM HALF INTEGRAL */
        { 0x08a6, 0x2502 }, /* ? BOX DRAWINGS LIGHT VERTICAL */
        { 0x08a7, 0x23a1 }, /* ? ??? */
        { 0x08a8, 0x23a3 }, /* ? ??? */
        { 0x08a9, 0x23a4 }, /* ? ??? */
        { 0x08aa, 0x23a6 }, /* ? ??? */
        { 0x08ab, 0x239b }, /* ? ??? */
        { 0x08ac, 0x239d }, /* ? ??? */
        { 0x08ad, 0x239e }, /* ? ??? */
        { 0x08ae, 0x23a0 }, /* ? ??? */
        { 0x08af, 0x23a8 }, /* ? ??? */
        { 0x08b0, 0x23ac }, /* ? ??? */
/*  0x08b1                     */
/*  0x08b2                     */
/*  0x08b3                 top */
/*  0x08b4                 bot */
/*  0x08b5                     */
/*  0x08b6                     */
/*  0x08b7                     */
        { 0x08bc, 0x2264 }, /* ? LESS-THAN OR EQUAL TO */
        { 0x08bd, 0x2260 }, /* ? NOT EQUAL TO */
        { 0x08be, 0x2265 }, /* ? GREATER-THAN OR EQUAL TO */
        { 0x08bf, 0x222b }, /* ? INTEGRAL */
        { 0x08c0, 0x2234 }, /* ? THEREFORE */
        { 0x08c1, 0x221d }, /* ? PROPORTIONAL TO */
        { 0x08c2, 0x221e }, /* ? INFINITY */
        { 0x08c5, 0x2207 }, /* ? NABLA */
        { 0x08c8, 0x223c }, /* ? TILDE OPERATOR */
        { 0x08c9, 0x2243 }, /* ? ASYMPTOTICALLY EQUAL TO */
        { 0x08cd, 0x21d4 }, /* ? LEFT RIGHT DOUBLE ARROW */
        { 0x08ce, 0x21d2 }, /* ? RIGHTWARDS DOUBLE ARROW */
        { 0x08cf, 0x2261 }, /* ? IDENTICAL TO */
        { 0x08d6, 0x221a }, /* ? SQUARE ROOT */
        { 0x08da, 0x2282 }, /* ? SUBSET OF */
        { 0x08db, 0x2283 }, /* ? SUPERSET OF */
        { 0x08dc, 0x2229 }, /* ? INTERSECTION */
        { 0x08dd, 0x222a }, /* ? UNION */
        { 0x08de, 0x2227 }, /* ? LOGICAL AND */
        { 0x08df, 0x2228 }, /* ? LOGICAL OR */
        { 0x08ef, 0x2202 }, /* ? PARTIAL DIFFERENTIAL */
        { 0x08f6, 0x0192 }, /* ? LATIN SMALL LETTER F WITH HOOK */
        { 0x08fb, 0x2190 }, /* ? LEFTWARDS ARROW */
        { 0x08fc, 0x2191 }, /* ? UPWARDS ARROW */
        { 0x08fd, 0x2192 }, /* ? RIGHTWARDS ARROW */
        { 0x08fe, 0x2193 }, /* ? DOWNWARDS ARROW */
/*  0x09df                     */
        { 0x09e0, 0x25c6 }, /* ? BLACK DIAMOND */
        { 0x09e1, 0x2592 }, /* ? MEDIUM SHADE */
        { 0x09e2, 0x2409 }, /* ? SYMBOL FOR HORIZONTAL TABULATION */
        { 0x09e3, 0x240c }, /* ? SYMBOL FOR FORM FEED */
        { 0x09e4, 0x240d }, /* ? SYMBOL FOR CARRIAGE RETURN */
        { 0x09e5, 0x240a }, /* ? SYMBOL FOR LINE FEED */
        { 0x09e8, 0x2424 }, /* ? SYMBOL FOR NEWLINE */
        { 0x09e9, 0x240b }, /* ? SYMBOL FOR VERTICAL TABULATION */
        { 0x09ea, 0x2518 }, /* ? BOX DRAWINGS LIGHT UP AND LEFT */
        { 0x09eb, 0x2510 }, /* ? BOX DRAWINGS LIGHT DOWN AND LEFT */
        { 0x09ec, 0x250c }, /* ? BOX DRAWINGS LIGHT DOWN AND RIGHT */
        { 0x09ed, 0x2514 }, /* ? BOX DRAWINGS LIGHT UP AND RIGHT */
        { 0x09ee, 0x253c }, /* ? BOX DRAWINGS LIGHT VERTICAL AND HORIZONTAL */
        { 0x09ef, 0x23ba }, /* ? HORIZONTAL SCAN LINE-1 (Unicode 3.2 draft) */
        { 0x09f0, 0x23bb }, /* ? HORIZONTAL SCAN LINE-3 (Unicode 3.2 draft) */
        { 0x09f1, 0x2500 }, /* ? BOX DRAWINGS LIGHT HORIZONTAL */
        { 0x09f2, 0x23bc }, /* ? HORIZONTAL SCAN LINE-7 (Unicode 3.2 draft) */
        { 0x09f3, 0x23bd }, /* ? HORIZONTAL SCAN LINE-9 (Unicode 3.2 draft) */
        { 0x09f4, 0x251c }, /* ? BOX DRAWINGS LIGHT VERTICAL AND RIGHT */
        { 0x09f5, 0x2524 }, /* ? BOX DRAWINGS LIGHT VERTICAL AND LEFT */
        { 0x09f6, 0x2534 }, /* ? BOX DRAWINGS LIGHT UP AND HORIZONTAL */
        { 0x09f7, 0x252c }, /* ? BOX DRAWINGS LIGHT DOWN AND HORIZONTAL */
        { 0x09f8, 0x2502 }, /* ? BOX DRAWINGS LIGHT VERTICAL */
        { 0x0aa1, 0x2003 }, /* ? EM SPACE */
        { 0x0aa2, 0x2002 }, /* ? EN SPACE */
        { 0x0aa3, 0x2004 }, /* ? THREE-PER-EM SPACE */
        { 0x0aa4, 0x2005 }, /* ? FOUR-PER-EM SPACE */
        { 0x0aa5, 0x2007 }, /* ? FIGURE SPACE */
        { 0x0aa6, 0x2008 }, /* ? PUNCTUATION SPACE */
        { 0x0aa7, 0x2009 }, /* ? THIN SPACE */
        { 0x0aa8, 0x200a }, /* ? HAIR SPACE */
        { 0x0aa9, 0x2014 }, /* ? EM DASH */
        { 0x0aaa, 0x2013 }, /* ? EN DASH */
/*  0x0aac                     */
        { 0x0aae, 0x2026 }, /* ? HORIZONTAL ELLIPSIS */
        { 0x0aaf, 0x2025 }, /* ? TWO DOT LEADER */
        { 0x0ab0, 0x2153 }, /* ? VULGAR FRACTION ONE THIRD */
        { 0x0ab1, 0x2154 }, /* ? VULGAR FRACTION TWO THIRDS */
        { 0x0ab2, 0x2155 }, /* ? VULGAR FRACTION ONE FIFTH */
        { 0x0ab3, 0x2156 }, /* ? VULGAR FRACTION TWO FIFTHS */
        { 0x0ab4, 0x2157 }, /* ? VULGAR FRACTION THREE FIFTHS */
        { 0x0ab5, 0x2158 }, /* ? VULGAR FRACTION FOUR FIFTHS */
        { 0x0ab6, 0x2159 }, /* ? VULGAR FRACTION ONE SIXTH */
        { 0x0ab7, 0x215a }, /* ? VULGAR FRACTION FIVE SIXTHS */
        { 0x0ab8, 0x2105 }, /* ? CARE OF */
        { 0x0abb, 0x2012 }, /* ? FIGURE DASH */
        { 0x0abc, 0x2329 }, /* ? LEFT-POINTING ANGLE BRACKET */
/*  0x0abd                     */
        { 0x0abe, 0x232a }, /* ? RIGHT-POINTING ANGLE BRACKET */
/*  0x0abf                     */
        { 0x0ac3, 0x215b }, /* ? VULGAR FRACTION ONE EIGHTH */
        { 0x0ac4, 0x215c }, /* ? VULGAR FRACTION THREE EIGHTHS */
        { 0x0ac5, 0x215d }, /* ? VULGAR FRACTION FIVE EIGHTHS */
        { 0x0ac6, 0x215e }, /* ? VULGAR FRACTION SEVEN EIGHTHS */
        { 0x0ac9, 0x2122 }, /* ? TRADE MARK SIGN */
        { 0x0aca, 0x2613 }, /* ? SALTIRE */
/*  0x0acb                     */
        { 0x0acc, 0x25c1 }, /* ? WHITE LEFT-POINTING TRIANGLE */
        { 0x0acd, 0x25b7 }, /* ? WHITE RIGHT-POINTING TRIANGLE */
        { 0x0ace, 0x25cb }, /* ? WHITE CIRCLE */
        { 0x0acf, 0x25af }, /* ? WHITE VERTICAL RECTANGLE */
        { 0x0ad0, 0x2018 }, /* ? LEFT SINGLE QUOTATION MARK */
        { 0x0ad1, 0x2019 }, /* ? RIGHT SINGLE QUOTATION MARK */
        { 0x0ad2, 0x201c }, /* ? LEFT DOUBLE QUOTATION MARK */
        { 0x0ad3, 0x201d }, /* ? RIGHT DOUBLE QUOTATION MARK */
        { 0x0ad4, 0x211e }, /* ? PRESCRIPTION TAKE */
        { 0x0ad6, 0x2032 }, /* ? PRIME */
        { 0x0ad7, 0x2033 }, /* ? DOUBLE PRIME */
        { 0x0ad9, 0x271d }, /* ? LATIN CROSS */
/*  0x0ada                     */
        { 0x0adb, 0x25ac }, /* ? BLACK RECTANGLE */
        { 0x0adc, 0x25c0 }, /* ? BLACK LEFT-POINTING TRIANGLE */
        { 0x0add, 0x25b6 }, /* ? BLACK RIGHT-POINTING TRIANGLE */
        { 0x0ade, 0x25cf }, /* ? BLACK CIRCLE */
        { 0x0adf, 0x25ae }, /* ? BLACK VERTICAL RECTANGLE */
        { 0x0ae0, 0x25e6 }, /* ? WHITE BULLET */
        { 0x0ae1, 0x25ab }, /* ? WHITE SMALL SQUARE */
        { 0x0ae2, 0x25ad }, /* ? WHITE RECTANGLE */
        { 0x0ae3, 0x25b3 }, /* ? WHITE UP-POINTING TRIANGLE */
        { 0x0ae4, 0x25bd }, /* ? WHITE DOWN-POINTING TRIANGLE */
        { 0x0ae5, 0x2606 }, /* ? WHITE STAR */
        { 0x0ae6, 0x2022 }, /* ? BULLET */
        { 0x0ae7, 0x25aa }, /* ? BLACK SMALL SQUARE */
        { 0x0ae8, 0x25b2 }, /* ? BLACK UP-POINTING TRIANGLE */
        { 0x0ae9, 0x25bc }, /* ? BLACK DOWN-POINTING TRIANGLE */
        { 0x0aea, 0x261c }, /* ? WHITE LEFT POINTING INDEX */
        { 0x0aeb, 0x261e }, /* ? WHITE RIGHT POINTING INDEX */
        { 0x0aec, 0x2663 }, /* ? BLACK CLUB SUIT */
        { 0x0aed, 0x2666 }, /* ? BLACK DIAMOND SUIT */
        { 0x0aee, 0x2665 }, /* ? BLACK HEART SUIT */
        { 0x0af0, 0x2720 }, /* ? MALTESE CROSS */
        { 0x0af1, 0x2020 }, /* ? DAGGER */
        { 0x0af2, 0x2021 }, /* ? DOUBLE DAGGER */
        { 0x0af3, 0x2713 }, /* ? CHECK MARK */
        { 0x0af4, 0x2717 }, /* ? BALLOT X */
        { 0x0af5, 0x266f }, /* ? MUSIC SHARP SIGN */
        { 0x0af6, 0x266d }, /* ? MUSIC FLAT SIGN */
        { 0x0af7, 0x2642 }, /* ? MALE SIGN */
        { 0x0af8, 0x2640 }, /* ? FEMALE SIGN */
        { 0x0af9, 0x260e }, /* ? BLACK TELEPHONE */
        { 0x0afa, 0x2315 }, /* ? TELEPHONE RECORDER */
        { 0x0afb, 0x2117 }, /* ? SOUND RECORDING COPYRIGHT */
        { 0x0afc, 0x2038 }, /* ? CARET */
        { 0x0afd, 0x201a }, /* ? SINGLE LOW-9 QUOTATION MARK */
        { 0x0afe, 0x201e }, /* ? DOUBLE LOW-9 QUOTATION MARK */
/*  0x0aff                     */
        { 0x0ba3, 0x003c }, /* < LESS-THAN SIGN */
        { 0x0ba6, 0x003e }, /* > GREATER-THAN SIGN */
        { 0x0ba8, 0x2228 }, /* ? LOGICAL OR */
        { 0x0ba9, 0x2227 }, /* ? LOGICAL AND */
        { 0x0bc0, 0x00af }, /* ? MACRON */
        { 0x0bc2, 0x22a5 }, /* ? UP TACK */
        { 0x0bc3, 0x2229 }, /* ? INTERSECTION */
        { 0x0bc4, 0x230a }, /* ? LEFT FLOOR */
        { 0x0bc6, 0x005f }, /* _ LOW LINE */
        { 0x0bca, 0x2218 }, /* ? RING OPERATOR */
        { 0x0bcc, 0x2395 }, /* ? APL FUNCTIONAL SYMBOL QUAD */
        { 0x0bce, 0x22a4 }, /* ? DOWN TACK */
        { 0x0bcf, 0x25cb }, /* ? WHITE CIRCLE */
        { 0x0bd3, 0x2308 }, /* ? LEFT CEILING */
        { 0x0bd6, 0x222a }, /* ? UNION */
        { 0x0bd8, 0x2283 }, /* ? SUPERSET OF */
        { 0x0bda, 0x2282 }, /* ? SUBSET OF */
        { 0x0bdc, 0x22a2 }, /* ? RIGHT TACK */
        { 0x0bfc, 0x22a3 }, /* ? LEFT TACK */
        { 0x0cdf, 0x2017 }, /* ? DOUBLE LOW LINE */
        { 0x0ce0, 0x05d0 }, /* ? HEBREW LETTER ALEF */
        { 0x0ce1, 0x05d1 }, /* ? HEBREW LETTER BET */
        { 0x0ce2, 0x05d2 }, /* ? HEBREW LETTER GIMEL */
        { 0x0ce3, 0x05d3 }, /* ? HEBREW LETTER DALET */
        { 0x0ce4, 0x05d4 }, /* ? HEBREW LETTER HE */
        { 0x0ce5, 0x05d5 }, /* ? HEBREW LETTER VAV */
        { 0x0ce6, 0x05d6 }, /* ? HEBREW LETTER ZAYIN */
        { 0x0ce7, 0x05d7 }, /* ? HEBREW LETTER HET */
        { 0x0ce8, 0x05d8 }, /* ? HEBREW LETTER TET */
        { 0x0ce9, 0x05d9 }, /* ? HEBREW LETTER YOD */
        { 0x0cea, 0x05da }, /* ? HEBREW LETTER FINAL KAF */
        { 0x0ceb, 0x05db }, /* ? HEBREW LETTER KAF */
        { 0x0cec, 0x05dc }, /* ? HEBREW LETTER LAMED */
        { 0x0ced, 0x05dd }, /* ? HEBREW LETTER FINAL MEM */
        { 0x0cee, 0x05de }, /* ? HEBREW LETTER MEM */
        { 0x0cef, 0x05df }, /* ? HEBREW LETTER FINAL NUN */
        { 0x0cf0, 0x05e0 }, /* ? HEBREW LETTER NUN */
        { 0x0cf1, 0x05e1 }, /* ? HEBREW LETTER SAMEKH */
        { 0x0cf2, 0x05e2 }, /* ? HEBREW LETTER AYIN */
        { 0x0cf3, 0x05e3 }, /* ? HEBREW LETTER FINAL PE */
        { 0x0cf4, 0x05e4 }, /* ? HEBREW LETTER PE */
        { 0x0cf5, 0x05e5 }, /* ? HEBREW LETTER FINAL TSADI */
        { 0x0cf6, 0x05e6 }, /* ? HEBREW LETTER TSADI */
        { 0x0cf7, 0x05e7 }, /* ? HEBREW LETTER QOF */
        { 0x0cf8, 0x05e8 }, /* ? HEBREW LETTER RESH */
        { 0x0cf9, 0x05e9 }, /* ? HEBREW LETTER SHIN */
        { 0x0cfa, 0x05ea }, /* ? HEBREW LETTER TAV */
        { 0x0da1, 0x0e01 }, /* ? THAI CHARACTER KO KAI */
        { 0x0da2, 0x0e02 }, /* ? THAI CHARACTER KHO KHAI */
        { 0x0da3, 0x0e03 }, /* ? THAI CHARACTER KHO KHUAT */
        { 0x0da4, 0x0e04 }, /* ? THAI CHARACTER KHO KHWAI */
        { 0x0da5, 0x0e05 }, /* ? THAI CHARACTER KHO KHON */
        { 0x0da6, 0x0e06 }, /* ? THAI CHARACTER KHO RAKHANG */
        { 0x0da7, 0x0e07 }, /* ? THAI CHARACTER NGO NGU */
        { 0x0da8, 0x0e08 }, /* ? THAI CHARACTER CHO CHAN */
        { 0x0da9, 0x0e09 }, /* ? THAI CHARACTER CHO CHING */
        { 0x0daa, 0x0e0a }, /* ? THAI CHARACTER CHO CHANG */
        { 0x0dab, 0x0e0b }, /* ? THAI CHARACTER SO SO */
        { 0x0dac, 0x0e0c }, /* ? THAI CHARACTER CHO CHOE */
        { 0x0dad, 0x0e0d }, /* ? THAI CHARACTER YO YING */
        { 0x0dae, 0x0e0e }, /* ? THAI CHARACTER DO CHADA */
        { 0x0daf, 0x0e0f }, /* ? THAI CHARACTER TO PATAK */
        { 0x0db0, 0x0e10 }, /* ? THAI CHARACTER THO THAN */
        { 0x0db1, 0x0e11 }, /* ? THAI CHARACTER THO NANGMONTHO */
        { 0x0db2, 0x0e12 }, /* ? THAI CHARACTER THO PHUTHAO */
        { 0x0db3, 0x0e13 }, /* ? THAI CHARACTER NO NEN */
        { 0x0db4, 0x0e14 }, /* ? THAI CHARACTER DO DEK */
        { 0x0db5, 0x0e15 }, /* ? THAI CHARACTER TO TAO */
        { 0x0db6, 0x0e16 }, /* ? THAI CHARACTER THO THUNG */
        { 0x0db7, 0x0e17 }, /* ? THAI CHARACTER THO THAHAN */
        { 0x0db8, 0x0e18 }, /* ? THAI CHARACTER THO THONG */
        { 0x0db9, 0x0e19 }, /* ? THAI CHARACTER NO NU */
        { 0x0dba, 0x0e1a }, /* ? THAI CHARACTER BO BAIMAI */
        { 0x0dbb, 0x0e1b }, /* ? THAI CHARACTER PO PLA */
        { 0x0dbc, 0x0e1c }, /* ? THAI CHARACTER PHO PHUNG */
        { 0x0dbd, 0x0e1d }, /* ? THAI CHARACTER FO FA */
        { 0x0dbe, 0x0e1e }, /* ? THAI CHARACTER PHO PHAN */
        { 0x0dbf, 0x0e1f }, /* ? THAI CHARACTER FO FAN */
        { 0x0dc0, 0x0e20 }, /* ? THAI CHARACTER PHO SAMPHAO */
        { 0x0dc1, 0x0e21 }, /* ? THAI CHARACTER MO MA */
        { 0x0dc2, 0x0e22 }, /* ? THAI CHARACTER YO YAK */
        { 0x0dc3, 0x0e23 }, /* ? THAI CHARACTER RO RUA */
        { 0x0dc4, 0x0e24 }, /* ? THAI CHARACTER RU */
        { 0x0dc5, 0x0e25 }, /* ? THAI CHARACTER LO LING */
        { 0x0dc6, 0x0e26 }, /* ? THAI CHARACTER LU */
        { 0x0dc7, 0x0e27 }, /* ? THAI CHARACTER WO WAEN */
        { 0x0dc8, 0x0e28 }, /* ? THAI CHARACTER SO SALA */
        { 0x0dc9, 0x0e29 }, /* ? THAI CHARACTER SO RUSI */
        { 0x0dca, 0x0e2a }, /* ? THAI CHARACTER SO SUA */
        { 0x0dcb, 0x0e2b }, /* ? THAI CHARACTER HO HIP */
        { 0x0dcc, 0x0e2c }, /* ? THAI CHARACTER LO CHULA */
        { 0x0dcd, 0x0e2d }, /* ? THAI CHARACTER O ANG */
        { 0x0dce, 0x0e2e }, /* ? THAI CHARACTER HO NOKHUK */
        { 0x0dcf, 0x0e2f }, /* ? THAI CHARACTER PAIYANNOI */
        { 0x0dd0, 0x0e30 }, /* ? THAI CHARACTER SARA A */
        { 0x0dd1, 0x0e31 }, /* ? THAI CHARACTER MAI HAN-AKAT */
        { 0x0dd2, 0x0e32 }, /* ? THAI CHARACTER SARA AA */
        { 0x0dd3, 0x0e33 }, /* ? THAI CHARACTER SARA AM */
        { 0x0dd4, 0x0e34 }, /* ? THAI CHARACTER SARA I */
        { 0x0dd5, 0x0e35 }, /* ? THAI CHARACTER SARA II */
        { 0x0dd6, 0x0e36 }, /* ? THAI CHARACTER SARA UE */
        { 0x0dd7, 0x0e37 }, /* ? THAI CHARACTER SARA UEE */
        { 0x0dd8, 0x0e38 }, /* ? THAI CHARACTER SARA U */
        { 0x0dd9, 0x0e39 }, /* ? THAI CHARACTER SARA UU */
        { 0x0dda, 0x0e3a }, /* ? THAI CHARACTER PHINTHU */
/*  0x0dde                     */
        { 0x0ddf, 0x0e3f }, /* ? THAI CURRENCY SYMBOL BAHT */
        { 0x0de0, 0x0e40 }, /* ? THAI CHARACTER SARA E */
        { 0x0de1, 0x0e41 }, /* ? THAI CHARACTER SARA AE */
        { 0x0de2, 0x0e42 }, /* ? THAI CHARACTER SARA O */
        { 0x0de3, 0x0e43 }, /* ? THAI CHARACTER SARA AI MAIMUAN */
        { 0x0de4, 0x0e44 }, /* ? THAI CHARACTER SARA AI MAIMALAI */
        { 0x0de5, 0x0e45 }, /* ? THAI CHARACTER LAKKHANGYAO */
        { 0x0de6, 0x0e46 }, /* ? THAI CHARACTER MAIYAMOK */
        { 0x0de7, 0x0e47 }, /* ? THAI CHARACTER MAITAIKHU */
        { 0x0de8, 0x0e48 }, /* ? THAI CHARACTER MAI EK */
        { 0x0de9, 0x0e49 }, /* ? THAI CHARACTER MAI THO */
        { 0x0dea, 0x0e4a }, /* ? THAI CHARACTER MAI TRI */
        { 0x0deb, 0x0e4b }, /* ? THAI CHARACTER MAI CHATTAWA */
        { 0x0dec, 0x0e4c }, /* ? THAI CHARACTER THANTHAKHAT */
        { 0x0ded, 0x0e4d }, /* ? THAI CHARACTER NIKHAHIT */
        { 0x0df0, 0x0e50 }, /* ? THAI DIGIT ZERO */
        { 0x0df1, 0x0e51 }, /* ? THAI DIGIT ONE */
        { 0x0df2, 0x0e52 }, /* ? THAI DIGIT TWO */
        { 0x0df3, 0x0e53 }, /* ? THAI DIGIT THREE */
        { 0x0df4, 0x0e54 }, /* ? THAI DIGIT FOUR */
        { 0x0df5, 0x0e55 }, /* ? THAI DIGIT FIVE */
        { 0x0df6, 0x0e56 }, /* ? THAI DIGIT SIX */
        { 0x0df7, 0x0e57 }, /* ? THAI DIGIT SEVEN */
        { 0x0df8, 0x0e58 }, /* ? THAI DIGIT EIGHT */
        { 0x0df9, 0x0e59 }, /* ? THAI DIGIT NINE */
        { 0x0ea1, 0x3131 }, /* ? HANGUL LETTER KIYEOK */
        { 0x0ea2, 0x3132 }, /* ? HANGUL LETTER SSANGKIYEOK */
        { 0x0ea3, 0x3133 }, /* ? HANGUL LETTER KIYEOK-SIOS */
        { 0x0ea4, 0x3134 }, /* ? HANGUL LETTER NIEUN */
        { 0x0ea5, 0x3135 }, /* ? HANGUL LETTER NIEUN-CIEUC */
        { 0x0ea6, 0x3136 }, /* ? HANGUL LETTER NIEUN-HIEUH */
        { 0x0ea7, 0x3137 }, /* ? HANGUL LETTER TIKEUT */
        { 0x0ea8, 0x3138 }, /* ? HANGUL LETTER SSANGTIKEUT */
        { 0x0ea9, 0x3139 }, /* ? HANGUL LETTER RIEUL */
        { 0x0eaa, 0x313a }, /* ? HANGUL LETTER RIEUL-KIYEOK */
        { 0x0eab, 0x313b }, /* ? HANGUL LETTER RIEUL-MIEUM */
        { 0x0eac, 0x313c }, /* ? HANGUL LETTER RIEUL-PIEUP */
        { 0x0ead, 0x313d }, /* ? HANGUL LETTER RIEUL-SIOS */
        { 0x0eae, 0x313e }, /* ? HANGUL LETTER RIEUL-THIEUTH */
        { 0x0eaf, 0x313f }, /* ? HANGUL LETTER RIEUL-PHIEUPH */
        { 0x0eb0, 0x3140 }, /* ? HANGUL LETTER RIEUL-HIEUH */
        { 0x0eb1, 0x3141 }, /* ? HANGUL LETTER MIEUM */
        { 0x0eb2, 0x3142 }, /* ? HANGUL LETTER PIEUP */
        { 0x0eb3, 0x3143 }, /* ? HANGUL LETTER SSANGPIEUP */
        { 0x0eb4, 0x3144 }, /* ? HANGUL LETTER PIEUP-SIOS */
        { 0x0eb5, 0x3145 }, /* ? HANGUL LETTER SIOS */
        { 0x0eb6, 0x3146 }, /* ? HANGUL LETTER SSANGSIOS */
        { 0x0eb7, 0x3147 }, /* ? HANGUL LETTER IEUNG */
        { 0x0eb8, 0x3148 }, /* ? HANGUL LETTER CIEUC */
        { 0x0eb9, 0x3149 }, /* ? HANGUL LETTER SSANGCIEUC */
        { 0x0eba, 0x314a }, /* ? HANGUL LETTER CHIEUCH */
        { 0x0ebb, 0x314b }, /* ? HANGUL LETTER KHIEUKH */
        { 0x0ebc, 0x314c }, /* ? HANGUL LETTER THIEUTH */
        { 0x0ebd, 0x314d }, /* ? HANGUL LETTER PHIEUPH */
        { 0x0ebe, 0x314e }, /* ? HANGUL LETTER HIEUH */
        { 0x0ebf, 0x314f }, /* ? HANGUL LETTER A */
        { 0x0ec0, 0x3150 }, /* ? HANGUL LETTER AE */
        { 0x0ec1, 0x3151 }, /* ? HANGUL LETTER YA */
        { 0x0ec2, 0x3152 }, /* ? HANGUL LETTER YAE */
        { 0x0ec3, 0x3153 }, /* ? HANGUL LETTER EO */
        { 0x0ec4, 0x3154 }, /* ? HANGUL LETTER E */
        { 0x0ec5, 0x3155 }, /* ? HANGUL LETTER YEO */
        { 0x0ec6, 0x3156 }, /* ? HANGUL LETTER YE */
        { 0x0ec7, 0x3157 }, /* ? HANGUL LETTER O */
        { 0x0ec8, 0x3158 }, /* ? HANGUL LETTER WA */
        { 0x0ec9, 0x3159 }, /* ? HANGUL LETTER WAE */
        { 0x0eca, 0x315a }, /* ? HANGUL LETTER OE */
        { 0x0ecb, 0x315b }, /* ? HANGUL LETTER YO */
        { 0x0ecc, 0x315c }, /* ? HANGUL LETTER U */
        { 0x0ecd, 0x315d }, /* ? HANGUL LETTER WEO */
        { 0x0ece, 0x315e }, /* ? HANGUL LETTER WE */
        { 0x0ecf, 0x315f }, /* ? HANGUL LETTER WI */
        { 0x0ed0, 0x3160 }, /* ? HANGUL LETTER YU */
        { 0x0ed1, 0x3161 }, /* ? HANGUL LETTER EU */
        { 0x0ed2, 0x3162 }, /* ? HANGUL LETTER YI */
        { 0x0ed3, 0x3163 }, /* ? HANGUL LETTER I */
        { 0x0ed4, 0x11a8 }, /* ? HANGUL JONGSEONG KIYEOK */
        { 0x0ed5, 0x11a9 }, /* ? HANGUL JONGSEONG SSANGKIYEOK */
        { 0x0ed6, 0x11aa }, /* ? HANGUL JONGSEONG KIYEOK-SIOS */
        { 0x0ed7, 0x11ab }, /* ? HANGUL JONGSEONG NIEUN */
        { 0x0ed8, 0x11ac }, /* ? HANGUL JONGSEONG NIEUN-CIEUC */
        { 0x0ed9, 0x11ad }, /* ? HANGUL JONGSEONG NIEUN-HIEUH */
        { 0x0eda, 0x11ae }, /* ? HANGUL JONGSEONG TIKEUT */
        { 0x0edb, 0x11af }, /* ? HANGUL JONGSEONG RIEUL */
        { 0x0edc, 0x11b0 }, /* ? HANGUL JONGSEONG RIEUL-KIYEOK */
        { 0x0edd, 0x11b1 }, /* ? HANGUL JONGSEONG RIEUL-MIEUM */
        { 0x0ede, 0x11b2 }, /* ? HANGUL JONGSEONG RIEUL-PIEUP */
        { 0x0edf, 0x11b3 }, /* ? HANGUL JONGSEONG RIEUL-SIOS */
        { 0x0ee0, 0x11b4 }, /* ? HANGUL JONGSEONG RIEUL-THIEUTH */
        { 0x0ee1, 0x11b5 }, /* ? HANGUL JONGSEONG RIEUL-PHIEUPH */
        { 0x0ee2, 0x11b6 }, /* ? HANGUL JONGSEONG RIEUL-HIEUH */
        { 0x0ee3, 0x11b7 }, /* ? HANGUL JONGSEONG MIEUM */
        { 0x0ee4, 0x11b8 }, /* ? HANGUL JONGSEONG PIEUP */
        { 0x0ee5, 0x11b9 }, /* ? HANGUL JONGSEONG PIEUP-SIOS */
        { 0x0ee6, 0x11ba }, /* ? HANGUL JONGSEONG SIOS */
        { 0x0ee7, 0x11bb }, /* ? HANGUL JONGSEONG SSANGSIOS */
        { 0x0ee8, 0x11bc }, /* ? HANGUL JONGSEONG IEUNG */
        { 0x0ee9, 0x11bd }, /* ? HANGUL JONGSEONG CIEUC */
        { 0x0eea, 0x11be }, /* ? HANGUL JONGSEONG CHIEUCH */
        { 0x0eeb, 0x11bf }, /* ? HANGUL JONGSEONG KHIEUKH */
        { 0x0eec, 0x11c0 }, /* ? HANGUL JONGSEONG THIEUTH */
        { 0x0eed, 0x11c1 }, /* ? HANGUL JONGSEONG PHIEUPH */
        { 0x0eee, 0x11c2 }, /* ? HANGUL JONGSEONG HIEUH */
        { 0x0eef, 0x316d }, /* ? HANGUL LETTER RIEUL-YEORINHIEUH */
        { 0x0ef0, 0x3171 }, /* ? HANGUL LETTER KAPYEOUNMIEUM */
        { 0x0ef1, 0x3178 }, /* ? HANGUL LETTER KAPYEOUNPIEUP */
        { 0x0ef2, 0x317f }, /* ? HANGUL LETTER PANSIOS */
        { 0x0ef3, 0x3181 }, /* ? HANGUL LETTER YESIEUNG */
        { 0x0ef4, 0x3184 }, /* ? HANGUL LETTER KAPYEOUNPHIEUPH */
        { 0x0ef5, 0x3186 }, /* ? HANGUL LETTER YEORINHIEUH */
        { 0x0ef6, 0x318d }, /* ? HANGUL LETTER ARAEA */
        { 0x0ef7, 0x318e }, /* ? HANGUL LETTER ARAEAE */
        { 0x0ef8, 0x11eb }, /* ? HANGUL JONGSEONG PANSIOS */
        { 0x0ef9, 0x11f0 }, /* ? HANGUL JONGSEONG YESIEUNG */
        { 0x0efa, 0x11f9 }, /* ? HANGUL JONGSEONG YEORINHIEUH */
        { 0x0eff, 0x20a9 }, /* ? WON SIGN */
        //{ 0x13a4, 0x20ac }, /* ? EURO SIGN */
        { 0x13bc, 0x0152 }, /* ? LATIN CAPITAL LIGATURE OE */
        { 0x13bd, 0x0153 }, /* ? LATIN SMALL LIGATURE OE */
        { 0x13be, 0x0178 }, /* ? LATIN CAPITAL LETTER Y WITH DIAERESIS */
        { 0x20ac, 0x20ac }, /* ? EURO SIGN */
    };
    
    /** a map from unicode values to equivalent X keysyms */
    private static final Map<Short, Short> keysymMap =
            new TreeMap<Short, Short>();
    static {
        // populate the table from the map above
        for (short[] mapping : map) {
            keysymMap.put(mapping[1], mapping[0]);
        }
    }

    /**
     * Gets an X11 keysym that matches the unicode.  If no
     * matches are found, returns a -1.
     *
     * @param unicode the unicode to convert
     * @return a matching X11 keysym or a -1 if no match was found.
     */
    public static int getKeysym(char unicode) {
        
        /* first check for Latin-1 characters (1:1 mapping)
         */
        if ((unicode >= 0x20 && unicode <= 0x7e)
            || (unicode >= 0xa0 && unicode <= 0xff)) {
            return unicode;
        }

        // search our map
        Short out = keysymMap.get((short) unicode);
        if (out != null) {
            return out.shortValue();
        }

        return -1;
    }
}
