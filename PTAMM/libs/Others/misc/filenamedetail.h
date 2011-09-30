#pragma  once
#include <string>

namespace FileName
{
	std::string find_filename_path(const std::string &filename);

	std::string find_filename_name(const std::string &filename);

	std::string find_filename_name_without_ext(const std::string &filename);

	std::string find_filename_ext(const std::string &filename);
}