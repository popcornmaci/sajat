#include <iostream>
#include <cstdlib>
#include <cstdio>
#include <fstream>
#include <osmium/visitor.hpp>
#include <osmium/memory/buffer.hpp>
#include <osmium/io/any_input.hpp>
#include "Typedefs.h"
#include "Route.h"

int main(int argc, char *argv[])
{
  if(argc !=6 && argc != 2)
  {
    std::cerr << "Usage: " << argv[0] <<
              " osmfile start_lat start_lon end_lat end_lon" << std::endl;
    exit(1);
  }
  
  double start_lat, start_lon, end_lat, end_lon;
  osmium::io::Reader
  reader(argv[1], osmium::osm_entity_bits::node | osmium::osm_entity_bits::way);
  osmium::memory::Buffer buffer = reader.read();
  reader.close();
  
  locations_t locations;
  location_handler_t lochandler(locations);
  
  osmium::apply(buffer, lochandler);
  
  if(argc == 2)
  {
    std::cout << "start_lat: ";
    std::cin >> start_lat;
    std::cout << "start_lon: ";
    std::cin >> start_lon;
    std::cout << "end_lat: ";
    std::cin >> end_lat;
    std::cout << "end_lon: ";
    std::cin >> end_lon;
  }
  else if(argc == 6)
  {
    start_lat = std::atof(argv[2]);
    start_lon = std::atof(argv[3]);
    end_lat= std::atof(argv[4]);
    end_lon= std::atof(argv[5]);
  }

    osmium::Location start(start_lon, start_lat);
    osmium::Location dest(end_lon,end_lat);
    Route r(buffer, locations);
    osmium::apply(buffer, r);
  
    path_t path=r.getPath(start,dest);
    
    for (const auto& l : path)
    {
      std::cout <<"lat= "<< l.lat() << "\t" <<"lon= "<< l.lon() << std::endl;
    }
    
    std::ofstream file;
    file.open("Route.xml");
    file <<  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" << std::endl;
    file << "<Route>" << std::endl;
    for (const auto& l : path)
    {
      file << "<Location lat="<<'"'<<l.lat() <<'"'<<"\tlon=" <<'"'<< l.lon() <<"\"/>"<< std::endl;
    }
    file << "</Route>";
    file.close();
    
  return 0;
}