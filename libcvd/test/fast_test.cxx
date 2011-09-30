#include <cvd/fast_corner.h>
#include <cvd/image_io.h>
#include <cvd/gl_helpers.h>
#include <cvd/videodisplay.h>
#include <utility>
#include <algorithm>
#include <iterator>
#include <iostream>


using namespace std;
namespace CVD
{
	void fast_corner_detect_plain_9(const SubImage<byte>& i, vector<ImageRef>& corners, int b);
	void fast_corner_detect_plain_10(const SubImage<byte>& i, vector<ImageRef>& corners, int b);
	void fast_corner_detect_plain_12(const SubImage<byte>& i, vector<ImageRef>& corners, int b);
}

using namespace CVD;

inline bool positive(int val, int centre, int barrier)
{
	return val > centre + barrier;
}

inline bool negative(int val, int centre, int barrier)
{
	return val < centre - barrier;
}

template<int num_for_corner, class Test>
inline int is_corner(const SubImage<byte>& im, const ImageRef off, int barrier, const Test& test)
{
	int num_consecutive=0;
	int first_cons=0;
	const int centre = im[off];

	for(int i=0; i<16; i++)
	{
		int val = im[fast_pixel_ring[i] + off];
		if(test(val, centre, barrier))
		{
			num_consecutive++;
			
			if(num_consecutive == num_for_corner)
				return 1;
		} 
		else
		{
			if(num_consecutive == i)
				first_cons=i;

			num_consecutive=0;
		}
	}
	
	return first_cons+num_consecutive >=num_for_corner;
}

template<int Num> void segment_test(const SubImage<byte>& im, vector<ImageRef>& v, int threshold)
{
	for(int y=3; y < im.size().y-3; y++)
		for(int x=3; x < im.size().x-3; x++)
			if(is_corner<Num>(im, ImageRef(x, y), threshold, positive) || is_corner<Num>(im, ImageRef(x, y), threshold, negative))
				v.push_back(ImageRef(x, y));	
}





template<class A, class B, class C> void test(const Image<byte>& i, A funcf, B funcp, C funcs, int threshold)
{
	cout << "Size: " << i.size() << " threshold: " << threshold << " ";

	vector<ImageRef> faster, normal, simple;

	funcp(i, normal, threshold);
	funcf(i, faster, threshold);
	funcs(i, simple, threshold);


	sort(normal.begin(), normal.end());
	sort(faster.begin(), faster.end());
	sort(simple.begin(), simple.end());

	vector<ImageRef> all;
	copy(normal.begin(), normal.end(), back_inserter(all));
	copy(faster.begin(), faster.end(), back_inserter(all));
	copy(simple.begin(), simple.end(), back_inserter(all));

	sort(all.begin(), all.end());
	vector<ImageRef>::iterator new_end = unique(all.begin(), all.end());
	all.resize(new_end - all.begin());

	cout << normal.size() << " " << faster.size() << " " << simple.size() << " " << all.size() << " ";



	vector<ImageRef> bad;
	set_symmetric_difference(faster.begin(), faster.end(), simple.begin(), simple.end(), back_inserter(bad));
	set_symmetric_difference(normal.begin(), normal.end(), simple.begin(), simple.end(), back_inserter(bad));

	if(bad.empty())
	{
		cout << "ok." << endl;
		return;
	}

	VideoDisplay d(i.size(), 2);

	glDrawPixels(i);
	glPointSize(3);
	glBegin(GL_POINTS);

	for(unsigned int i=0; i < all.size(); i++)
	{
		Rgb<byte> colour(0,0,0);

		if(!binary_search(normal.begin(), normal.end(), all[i]))
			colour.red = 255;

		if(!binary_search(faster.begin(), faster.end(), all[i]))
			colour.green = 255;

		if(!binary_search(simple.begin(),simple.end(), all[i]))
			colour.blue = 255;

		//Colour can never be white. Black means OK.

		if(colour != Rgb<byte>(0,0,0))
		{
			glColor(colour);
			glVertex(all[i]);
		}
	}
	glEnd();

	cout << "\x1b[31m BROKEN!\x1b[0m\n";

	cin.get();
}

template<class A, class B, class C> void test_images(const Image<byte>& im, A funcf, B funcp, C funcs, int threshold)
{
	ImageRef one(1,1);
	ImageRef zero(0,0);

	for(int i=0; i < 16; i++)
	{
		ImageRef size = im.size() - i * one;

		Image<byte> part(size);
		SubImage<byte> s = im.sub_image(zero, size);
		copy(s.begin(), s.end(),part.begin());

		test(part, funcf, funcp, funcs, threshold);
	}
}


int main(int argc, char** argv)
{

	for(;;)
	{
		Image<byte> im(ImageRef(drand48() * 1024 + 16, drand48()*1024 + 16));

		for(byte* i = im.begin(); i != im.end(); i++)
			*i =  (drand48() * 256);

		int threshold = drand48() * 256;
	
		cout << "*************************FAST-9\n";
		test_images(im, fast_corner_detect_9, fast_corner_detect_plain_9, segment_test<9>, threshold);

		cout << "*************************FAST-10\n";
		test_images(im, fast_corner_detect_10, fast_corner_detect_plain_10, segment_test<10>, threshold);

		cout << "*************************FAST-12\n";
		test_images(im, fast_corner_detect_12, fast_corner_detect_plain_12, segment_test<12>, threshold);
		
	}
}
