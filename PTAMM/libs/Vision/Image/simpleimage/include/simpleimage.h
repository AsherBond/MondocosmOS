// simpleimage.h: interface for the CSimpleImage class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_SIMPLEIMAGE_H__33211B25_D806_4947_B5E6_8D8DEF293501__INCLUDED_)
#define AFX_SIMPLEIMAGE_H__33211B25_D806_4947_B5E6_8D8DEF293501__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

//#ifndef byte
//#define unsigned char byte
//#endif
//////////////////////////////////////////////////////////////////////////
/// \ingroup Buffer
/// \brief Most simple image class.
///
/// Store the image data, and provides basic access operation.
//////////////////////////////////////////////////////////////////////////
template <class T>
class CSimpleImage  
{
public:
	CSimpleImage():_width(0), _height(0), _channel(0), _data(0), _effwidth(0), _size(0), _rowpointer(0)
	{

	}
	CSimpleImage(const CSimpleImage<T> &object)
	{
		_data = 0;
		_rowpointer = 0;
		_width = 0;
		_height = 0;
		_size = 0;
		_effwidth = 0;
		_channel = 0;

		if(object._data != 0)
			set_data(object._data, object._width, object._height, object._channel);
	}

	virtual ~CSimpleImage()
	{
		if (_data != 0)
		{
			delete []_data;
			_data = 0;
		}

		if (_rowpointer != 0)
		{
			delete []_rowpointer;
			_rowpointer = 0;
		}
	}
	
	T *_data;
	T **_rowpointer;
	int _height;
	int _width;
	int _channel;
	int _effwidth;
	int _size;

	typedef T datatype;

	//////////////////////////////////////////////////////////////////////////
	/// \brief Access the image data.
	///
	/// Without out of index check!
	///
	/// \param x [in] The [x] coordinate.
	/// \param y [in] The [y] coordinate.
	/// \param channel [in] The channel index.
	/// \return The channel value.
	//////////////////////////////////////////////////////////////////////////
	//T& operator()(int x, int y, int channel=0)
	//{
	//	return _rowpointer[y][x*_channel + channel];
	//}

	//const T& operator()(int x, int y, int channel=0) const
	//{
	//	return _rowpointer[y][x*_channel + channel];
	//}

	T* operator()(int x, int y) const
	{
		return _rowpointer[y] + x*_channel;
	}


	//T& at(int x, int y, int channel=0)
	//{
	//	return _rowpointer[y][x*_channel + channel];
	//}

	//const T& at(int x, int y, int channel=0) const
	//{
	//	return _rowpointer[y][x*_channel + channel];
	//}

	T* at(int x, int y)
	{
		return _rowpointer[y] + x*_channel;
	}

	CSimpleImage<T> & operator =(const CSimpleImage<T> &object)
	{
		if (this != &object && object._data != 0)
		{
			set_data(object._data, object._width, object._height, object._channel);
		}		

		return (*this);
	}

	void set_size(int width, int height, int channel)
	{
		alloc_memory(width, height, channel);
	}

	void set_data(T* data, int width, int height, int channel)
	{
		if (alloc_memory(width, height, channel))
		{
			memcpy(_data, data, _size*sizeof(T));
		}
	}

	const bool is_valid() const
	{
		return (_data != 0 );
	}

	void clear()
	{
		if (_data != 0)
		{
			delete []_data;
			_data = 0;
		}

		if (_rowpointer != 0)
		{
			delete []_rowpointer;
			_rowpointer = 0;
		}
	}
protected:
	bool alloc_memory(int width, int height, int channel)
	{
		if (width == 0 || height == 0 || channel == 0)
		{
			return false;
		}

		int new_effwidth = (channel*width*sizeof(T)+3)/4*4/sizeof(T);
		//int new_effwidth = ((((wBpp * dwWidth) + 31) / 32) * 4);


		int new_size = new_effwidth * height;

		if (new_size != _size || _data == 0)
		{
			if (_data != 0)
			{
				delete []_data;
				_data = 0;

				delete []_rowpointer;
				_rowpointer = 0;
			}
			_data = new T[new_size];

			_rowpointer = new T*[height];

			for (int row = 0; row < height; ++ row)
			{
				_rowpointer[row] = _data + row * new_effwidth;
			}

			_size = new_size;
			_effwidth = new_effwidth;
			_width = width;
			_height = height;
			_channel = channel;
		}

		return (_data != 0 && _rowpointer != 0);
	}

};

typedef CSimpleImage<double> CSimpleImaged;
typedef CSimpleImage<int> CSimpleImagei;
typedef CSimpleImage<float> CSimpleImagef;
typedef CSimpleImage<unsigned char> CSimpleImageb;

//template class __declspec(dllexport) CSimpleImage<640, 480, 3>;

#endif // !defined(AFX_SIMPLEIMAGE_H__33211B25_D806_4947_B5E6_8D8DEF293501__INCLUDED_)
