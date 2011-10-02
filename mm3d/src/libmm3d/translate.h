/*  Misfit Model 3D
 * 
 *  Copyright (c) 2004-2007 Kevin Worcester
 * 
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, 
 *  USA.
 *
 *  See the COPYING file for full license text.
 */


#ifndef __TRANSLATE_H
#define __TRANSLATE_H

#include <string>
#include <stdlib.h>

#undef  QT_TRANSLATE_NOOP
#define QT_TRANSLATE_NOOP( a, b ) b

typedef std::string (*TransLLCallbackF)( const char * str );

std::string transll( const char * str );

void transll_install_handler( TransLLCallbackF );

#endif // __TRANSLATE_H
