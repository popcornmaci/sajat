#include <osmium/handler.hpp>
#include "Typedefs.h"

class NearestNode : public osmium::handler::Handler
{
  const osmium::Location &loc;
  double min = 100000.0;
  osmium::unsigned_object_id_type node_id;
  const locations_t& locations;

public:
  NearestNode(const osmium::Location &loc, const locations_t& locations):
  loc(loc),locations(locations)
  {}
  void way(const osmium::Way &way)
  {
    const osmium::WayNodeList& wayNodes = way.nodes();
    for (const osmium::NodeRef& nodeR : wayNodes)
    {
      osmium::Location temp = locations.get(nodeR.positive_ref());
      double dx = loc.lon() - temp.lon();
      double dy = loc.lat() - temp.lat();
      double dist = dx*dx + dy*dy;
      if (dist < min)
      {
	min = dist;
	node_id = nodeR.positive_ref();
      }
    }
  }
  osmium::unsigned_object_id_type get() const
  {
    return node_id;
  }
}; 