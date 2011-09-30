#pragma once
#include "../mtbuffer/videoframe.h"

/// Initialize the internal parameters of artoolkit.
void init_cparam(int width, int height, double focal);

/// Initialize the external paramters of the camera.
bool arToolkitGetTrans(const marker_info& marker, MARKER_FLOAT P[3][4]);
bool arToolkitGetTrans(const siftmarker& marker, MARKER_FLOAT P[3][4]);