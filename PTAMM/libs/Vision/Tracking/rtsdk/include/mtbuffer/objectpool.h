// objectpool.h: interface for the ObjectPool class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_OBJECTPOOL_H__FC43D48B_F676_4279_9E62_CF41D8C0D236__INCLUDED_)
#define AFX_OBJECTPOOL_H__FC43D48B_F676_4279_9E62_CF41D8C0D236__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

// disable the boost auto link feature.
#ifndef BOOST_THREAD_NO_LIB
#define BOOST_THREAD_NO_LIB
#endif

#include <list>
#include <boost/thread/mutex.hpp>

//////////////////////////////////////////////////////////////////////////
/// \ingroup Buffer
/// \brief A simple object pool.
///
/// Simple object pool with fixed capacity. 
//////////////////////////////////////////////////////////////////////////
template <class T>
class object_pool  
{
	/// \brief Scoped lock of boost::thread.
	typedef boost::mutex::scoped_lock
        scoped_lock;

public:
	object_pool(int capacity)
	{
		for (int i = 0; i < capacity; ++ i)
		{
			T* temp = new T();
			_available.push_back(temp);
			_all.push_back(temp);
		}
	}
	virtual ~object_pool()
	{
		std::list<T*>::iterator it = _all.begin();
		for ( ; it != _all.end(); ++ it)
		{
			delete (*it);
		}
	}

	T* get()
	{
		scoped_lock lock(_mutex);

		while (_available.empty())
		{
			_emptycond.wait(lock);
		}
		
		T* temp = _available.back();
		_available.pop_back();
		return temp;
	}

	void put(T* t)
	{
		scoped_lock lock(_mutex);

		_available.push_back(t);
		
		_emptycond.notify_one();
	}
	
protected:
	std::list<T*> _available;
	std::list<T*> _all;

	//////////////////////////////////////////////////////////////////////////
	///
	boost::mutex _mutex;


	//////////////////////////////////////////////////////////////////////////
	///
	boost::condition _emptycond;
};

#endif // !defined(AFX_OBJECTPOOL_H__FC43D48B_F676_4279_9E62_CF41D8C0D236__INCLUDED_)
