// simpleimageconvert.h: interface for the CSimpleImage class.
//
//////////////////////////////////////////////////////////////////////

#pragma once

#include "simpleimage.h"
#include <limits>
#include <vector>

namespace SimpleImage
{
	template<class T>
	bool equal(const CSimpleImage<T> &src, CSimpleImage<T> &dest)
	{
		if (!src.is_valid() || !dest.is_valid())
		{
			return false;
		}
		
		if (src._width != dest._width || src._height != dest._height || src._channel != dest._channel)
		{
			return false;
		}
		
		return (memcmp(src._data, dest._data, src._size) == 0);
	}

	template<class T>
	bool divide(const CSimpleImage<T> &src, CSimpleImage<T> &dst, T divisor)
	{
		if (!src.is_valid())
		{
			return false;
		}

		dst = src;

		int width = dst._width;
		int height = dst._height;

		if (dst._channel == 1)
		{
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					dst(x, y)[0] /= divisor;
				}
			}	
		}
		else if (dst._channel == 3)
		{
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					T *pixel = dst.at(x, y);
					pixel[0] /= divisor;
					pixel[1] /= divisor;
					pixel[2] /= divisor;
				}
			}	
		}
		
		return true;
	}

	template<class T, class T1>
	bool divide(CSimpleImage<T> &src, T1 divisor)
	{
		if (!src.is_valid())
		{
			return false;
		}

		int width = src._width;
		int height = src._height;

		if (src._channel == 1)
		{
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					src(x, y)[0] /= divisor;
				}
			}	
		}
		else if (src._channel == 3)
		{
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					T *pixel = src.at(x, y);
					pixel[0] /= divisor;
					pixel[1] /= divisor;
					pixel[2] /= divisor;
				}
			}	
		}
		return true;
	}

	template<class T, class T1>
	bool multiply(CSimpleImage<T> &src, T1 multiplier)
	{
		if (!src.is_valid())
		{
			return false;
		}

		int width = src._width;
		int height = src._height;

		if (src._channel == 1)
		{
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					src(x, y)[0] *= multiplier;
				}
			}	
		}
		else if (src._channel == 3)
		{
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					T *pixel = src.at(x, y);
					pixel[0] *= multiplier;
					pixel[1] *= multiplier;
					pixel[2] *= multiplier;
				}
			}	
		}
		return true;
	}


	template<class T>
	bool flip(CSimpleImage<T> &src)
	{
		if (!src.is_valid())
		{
			return false;
		}

		int height = src._height;

		CSimpleImage<T> *tmp = new CSimpleImage<T>(src);
		for (int row = 0; row < height; ++ row)
		{
			memcpy(src._rowpointer[row], tmp->_rowpointer[height - 1 - row], src._effwidth*sizeof(T));
		}
		delete tmp;

		return true;
	}

#undef max
#undef min
	template<class T>
	bool normalize(CSimpleImage<T> &src)
	{
		if (!src.is_valid())
		{
			return false;
		}

		int width = src._width;
		int height = src._height;

		if (src._channel == 1)
		{
			T minp = std::numeric_limits<T>::max();
			T maxp = -std::numeric_limits<T>::max();
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					T v = src(x, y)[0];
					if (v < minp)
					{
						minp = v;
					}
					else if (v > maxp)
					{
						maxp = v;
					}
				}
			}	


			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					T &v = src(x, y)[0];

					v = (v-minp)/(maxp-minp);
				}
			}	
		}
		else
		{
			return false;	
		}
		return true;
	}

	template<class T>
	bool normalize(CSimpleImage<T> &src, CSimpleImageb &mask)
	{
		if (!src.is_valid() || !mask.is_valid())
		{
			return false;
		}

		int width = src._width;
		int height = src._height;

		if (src._channel == 1)
		{
			T minp = std::numeric_limits<T>::max();
			T maxp = -std::numeric_limits<T>::max();
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					if (mask(x,y)[0] > 0)
					{
						T v = src(x, y)[0];
						if (v < minp)
						{
							minp = v;
						}
						else if (v > maxp)
						{
							maxp = v;
						}
					}
				}
			}	


			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					T &v = src(x, y)[0];

					if (mask(x, y)[0] > 0)
					{
						v = (v-minp)/(maxp-minp);
					}
					else
					{
						v = 0;
					}					
				}
			}	
		}
		else
		{
			return false;	
		}
		return true;
	}

	template<class T>
	bool minus(CSimpleImage<T> &src0, CSimpleImage<T> &src1, CSimpleImage<T> &dest)
	{
		if (src0._width != src1._width || src0._height != src1._height || src0._channel != src1._channel)
		{
			return false;
		}

		int width = src0._width;
		int height = src0._height;

		dest.set_size(width, height, src0._channel);

		for(int i = 0;i < height; i ++)
		{
			for(int j = 0; j < width; j ++)
			{
				dest._rowpointer[i][j] = src0._rowpointer[i][j] - src1._rowpointer[i][j];
			}
		}
		return true;
	}

	template<class T>
	bool add(CSimpleImage<T> &src0, CSimpleImage<T> &src1, CSimpleImage<T> &dst)
	{
		if (src0._width != src1._width || src0._height != src1._height || src0._channel != src1._channel)
		{
			return false;
		}

		int width = src0._width;
		int height = src0._height;

		dst.set_size(width, height, src0._channel);

		for(int y = 0;y < height; y ++)
		{
			for(int x = 0; x < width; x ++)
			{
				T *dstpixel = dst.at(x, y);
				T *pixel0 = src0.at(x, y);
				T *pixel1 = src1.at(x, y);

				for (int c = 0;  c < src0._channel; ++ c)
				{
					dstpixel[c] = pixel0[c] + pixel1[c];
				}
			}
		}
		return true;
	}

	template<class T>
	bool scalehalf(CSimpleImage<T> &src, CSimpleImage<T> &dst)
	{
		int nrow = src._height / 2;
		int ncol = src._width / 2;

		if(nrow == 0 || ncol == 0)
			return false;

		dst.set_size(ncol, nrow, src._channel);

		if (src._channel == 1)
		{
			for(int i=0;i<nrow;i++)
			{
				for(int j=0;j<ncol;j++)
				{
					dst._rowpointer[i][j] = src._rowpointer[i*2][j*2];
				} 
			}		
		}
		else if (src._channel == 3)
		{
			for(int i=0;i<nrow;i++)
			{
				for(int j=0;j<ncol;j++)
				{
					T *dstpixel = dst.at(j, i);
					T *pixel = src.at(j*2, i*2);
					
					dstpixel[0] = pixel[0];
					dstpixel[1] = pixel[1];
					dstpixel[2] = pixel[2];
				}
			}		
		}
		return true;
	}

	template<class T>
	bool scalehalf(CSimpleImage<T> &srcdst)
	{
		int nrow = srcdst._height / 2;
		int ncol = srcdst._width / 2;

		if(nrow == 0 || ncol == 0)
			return false;

		CSimpleImage<T> temp = srcdst;
		srcdst.set_size(ncol, nrow, srcdst._channel);

		if (srcdst._channel == 1)
		{
			for(int i=0;i<nrow;i++)
			{
				for(int j=0;j<ncol;j++)
				{
					srcdst._rowpointer[i][j] = temp._rowpointer[i*2][j*2];
				}
			}		
		}
		else if (srcdst._channel == 3)
		{
			for(int i=0;i<nrow;i++)
			{
				for(int j=0;j<ncol;j++)
				{
					T *dstpixel = srcdst.at(j, i);
					T *pixel = temp.at(j*2, i*2);

					dstpixel[0] = pixel[0];
					dstpixel[1] = pixel[1];
					dstpixel[2] = pixel[2];
				}
			}		
		}

		return true;
	}

	template<class T>
	bool zero(CSimpleImage<T> &srcdst)
	{
		memset(srcdst._data, 0, srcdst._size*sizeof(T));

		return true;
	}

	template<class T>
	bool setvalue(CSimpleImage<T> &srcdst, T value)
	{
		int nrow = srcdst._height;
		int ncol = srcdst._width;

		if (srcdst._channel == 1)
		{
			for(int i=0;i<nrow;i++)
			{
				for(int j=0;j<ncol;j++)
				{
					srcdst._rowpointer[i][j] = value;
				}
			}		
		}
		else if (srcdst._channel == 3)
		{
			for(int i=0;i<nrow;i++)
			{
				for(int j=0;j<ncol;j++)
				{
					T *dstpixel = srcdst.at(j, i);

					dstpixel[0] = value;
					dstpixel[1] = value;
					dstpixel[2] = value;
				}
			}		
		}
		return true;
	}

	template<class T>
	bool mean(CSimpleImage<T> &srcdst, std::vector<T> &value) 
	{
		int nrow = srcdst._height;
		int ncol = srcdst._width;
		value.clear();

		if (srcdst._channel == 1)
		{
			float sum = 0;
			for(int i = 0; i < nrow; i++)
			{
				for(int j = 0; j < ncol; j++)
				{
					sum += srcdst._rowpointer[i][j];
				}
			}	
			sum /= (nrow*ncol);

			value.push_back(sum);
		}
		else if (srcdst._channel == 3)
		{
			float sum[3] = {0.0f, 0.0f, 0.0f};
			for(int i = 0; i < nrow; i++)
			{
				for(int j = 0; j < ncol; j++)
				{
					T *dstpixel = srcdst.at(j, i);

					sum[0] += dstpixel[0];
					sum[1] += dstpixel[1];
					sum[2] += dstpixel[2];
				}
			}		

			sum[0] /= (nrow*ncol);
			sum[1] /= (nrow*ncol);
			sum[2] /= (nrow*ncol);

			value.push_back(sum[0]);
			value.push_back(sum[1]);
			value.push_back(sum[2]);
		}

		return true;
	}

	template<class T>
	bool mean(CSimpleImage<T> &srcdst, CSimpleImageb &mask, std::vector<T> &value) 
	{
		int nrow = srcdst._height;
		int ncol = srcdst._width;
		value.clear();

		if (srcdst._channel == 1)
		{
			float sum = 0;
			int count = 0;
			for(int i = 0; i < nrow; i++)
			{
				for(int j = 0; j < ncol; j++)
				{
					if (mask.at(i, j) > 0)
					{
						sum += srcdst._rowpointer[i][j];
						++ count;
					}
				}
			}	
			sum /= count;

			value.push_back(sum);
		}
		else if (srcdst._channel == 3)
		{
			float sum[3] = {0.0f, 0.0f, 0.0f};
			int count = 0;
			for(int i = 0; i < nrow; i++)
			{
				for(int j = 0; j < ncol; j++)
				{
					T *dstpixel = srcdst.at(j, i);
					if (mask.at(i, j) > 0)
					{
						sum[0] += dstpixel[0];
						sum[1] += dstpixel[1];
						sum[2] += dstpixel[2];

						++ count;
					}
				}
			}		

			sum[0] /= count;
			sum[1] /= count;
			sum[2] /= count;

			value.push_back(sum[0]);
			value.push_back(sum[1]);
			value.push_back(sum[2]);
		}

		return true;
	}

	template<class T>
	bool bilinear(CSimpleImage<T> &src, float x, float y, CSimpleImage<T> &dst, int nx, int ny) 
	{
		int srcx = x;
		int srcy = y;
		float dx = x - srcx;
		float dy = y - srcy;

		T* srcpixel0 = src.at(srcx, srcy);
		T* srcpixel1 = src.at(srcx + 1, srcy);
		T* srcpixel2 = src.at(srcx + 1, srcy + 1);
		T* srcpixel3 = src.at(srcx, srcy + 1);
		
		T* dstpixel = dst.at(nx, ny);

		if (src._channel == 1)
		{
			dstpixel[0] = (1.0f - dy)*((1.0f - dx)*srcpixel0[0] + dx * srcpixel1[0])
				* dy*((1.0f - dx)*srcpixel3[0] + dx * srcpixel2[0]);
		}
		else if (src._channel == 3)
		{
			dstpixel[0] = (1.0f - dy)*((1.0f - dx)*srcpixel0[0] + dx * srcpixel1[0])
				+ dy*((1.0f - dx)*srcpixel3[0] + dx * srcpixel2[0]);
			dstpixel[1] = (1.0f - dy)*((1.0f - dx)*srcpixel0[1] + dx * srcpixel1[1])
				+ dy*((1.0f - dx)*srcpixel3[1] + dx * srcpixel2[1]);
			dstpixel[2] = (1.0f - dy)*((1.0f - dx)*srcpixel0[2] + dx * srcpixel1[2])
				+ dy*((1.0f - dx)*srcpixel3[2] + dx * srcpixel2[2]);
		}

		return true;
	}

	template<class T>
	float clamp(T a) 
	{
		if (a < 0)
		{
			return 0;
		}
		else if (a > 255)
		{
			return 255;
		}
		return a;
	}
}
