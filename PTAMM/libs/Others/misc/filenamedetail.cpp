#include <string>
#include "filenamedetail.h"

namespace FileName
{
	std::string find_filename_path(const std::string &filename)
	{
		std::string::size_type pos = filename.rfind('/');
		std::string::size_type pos1 = filename.rfind('\\');

		if (pos == std::string::npos && pos1 != std::string::npos)
		{
			return filename.substr(0, pos1+1);			
		}
		
		if (pos != std::string::npos && pos1 == std::string::npos)
		{
			return filename.substr(0, pos+1);
		}

		if (pos != std::string::npos && pos1 != std::string::npos)
		{
			if (pos1 > pos)
			{
				return filename.substr(0, pos1+1);
			}
			else
			{
				return filename.substr(0, pos+1);
			}
		}
		return std::string();
	}

	std::string find_filename_name(const std::string &filename)
	{
		std::string::size_type pos = filename.rfind('/');
		std::string::size_type pos1 = filename.rfind('\\');

		if (pos == std::string::npos && pos1 != std::string::npos)
		{
			return filename.substr(pos1+1, filename.size());			
		}

		if (pos != std::string::npos && pos1 == std::string::npos)
		{
			return filename.substr(pos+1, filename.size());
		}

		if (pos != std::string::npos && pos1 != std::string::npos)
		{
			if (pos1 > pos)
			{
				return filename.substr(pos1+1, filename.size());
			}
			else
			{
				return filename.substr(pos+1, filename.size());
			}
		}
		return std::string();
	}

	std::string find_filename_name_without_ext(const std::string &filename)
	{
		std::string::size_type pos = filename.rfind('/');
		std::string::size_type pos1 = filename.rfind('\\');

		std::string::size_type pos2 = filename.rfind('.');
		if (pos2 == std::string::npos)
		{
			pos2 = filename.size();
		}

		if (pos == std::string::npos && pos1 != std::string::npos)
		{
			return filename.substr(pos1+1, pos2 - pos1 - 1);			
		}

		if (pos != std::string::npos && pos1 == std::string::npos)
		{
			return filename.substr(pos+1, pos2 - pos - 1);
		}

		if (pos != std::string::npos && pos1 != std::string::npos)
		{
			if (pos1 > pos)
			{
				return filename.substr(pos1+1, pos2 - pos1 - 1);
			}
			else
			{
				return filename.substr(pos+1, pos2 - pos - 1);
			}
		}
		return std::string();
	}

	std::string find_filename_ext(const std::string &filename)
	{
		std::string::size_type pos = filename.rfind('.');

		if (pos != std::string::npos)
		{
			return filename.substr(pos+1, filename.size());			
		}
		return std::string();
	}
}