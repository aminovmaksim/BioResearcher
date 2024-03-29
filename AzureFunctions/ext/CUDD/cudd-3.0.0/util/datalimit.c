/**
  @file

  @ingroup util

  @brief Access to datasize limit.

  @copyright@parblock
  Copyright (c) 1995-2015, Regents of the University of Colorado

  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions
  are met:

  Redistributions of source code must retain the above copyright
  notice, this list of conditions and the following disclaimer.

  Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.

  Neither the name of the University of Colorado nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
  FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
  COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
  ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
  POSSIBILITY OF SUCH DAMAGE.
  @endparblock

*/

#include "config.h"

#if HAVE_STDINT_H == 1
#include <stdint.h>
#endif
#if HAVE_STDDEF_H == 1
#include <stddef.h>
#endif

// In "datalimit.c": comment out #include <sys/resource.h> and change #include <sys/time.h> to #include <time.h>
//#if HAVE_SYS_RESOURCE_H == 1
//#if HAVE_SYS_TIME_H == 1
//#include <sys/time.h>
//#endif
//#include <sys/resource.h>
//#endif
#include <time.h>

#ifdef _WIN32
#include <windows.h>
#endif

/**
 * @brief Default value returned if getrlimit not available.
 */
//#ifndef RLIMIT_DATA_DEFAULT
//#define RLIMIT_DATA_DEFAULT 268435456	/* assume 256MB by default */
//#endif

/**
 * @def EXTERN
 * @brief Allows C linkage when compiling as C++.
 */
#ifndef EXTERN
#   ifdef __cplusplus
#	define EXTERN extern "C"
#   else
#	define EXTERN extern
#   endif
#endif

EXTERN size_t getSoftDataLimit(void);

/**
 * @brief Gets the soft datasize limit.
 */
size_t
getSoftDataLimit(void)
{
#if HAVE_SYS_RESOURCE_H == 1 && HAVE_GETRLIMIT == 1 && defined(RLIMIT_DATA)
    struct rlimit rl;
    int result;

    result = getrlimit(RLIMIT_DATA, &rl);
    if (result != 0 || rl.rlim_cur == RLIM_INFINITY)
	return (size_t) RLIMIT_DATA_DEFAULT;
    else
	return (size_t) rl.rlim_cur;
#elif defined(_WIN32)
    /* Not quite the same, because this returns available physical memory. */
    MEMORYSTATUSEX statex;
    statex.dwLength = sizeof(statex);
//    if (GlobalMemoryStatusEx(&statex))
    GlobalMemoryStatusEx(&statex);
	return (size_t) statex.ullTotalPhys;
//    else
//	return (size_t) RLIMIT_DATA_DEFAULT;
#else
    return (size_t) RLIMIT_DATA_DEFAULT;
#endif

} /* end of getSoftDataLimit */
