#include <osmium/handler.hpp>
#include <utility>
#include <osmium/geom/haversine.hpp>
#include <osmium/visitor.hpp>
#include <boost/config.hpp>
#include <boost/graph/dijkstra_shortest_paths.hpp>
#include "NearestNode.h"
#include "Typedefs.h"

class Route : public osmium::handler::Handler
{
  osmium::memory::Buffer& buffer;
  graph_t g;
  idvertex_map_t idvertex_map;
  vertexloc_map_t vertexloc_map;
  path_t path;
  const locations_t& locations;
  
public:
  Route(osmium::memory::Buffer &buffer, const locations_t& locations):
  buffer(buffer),locations(locations)
  {}
  void way(const osmium::Way& way)
  {
  const osmium::WayNodeList& wayNodes = way.nodes();
  osmium::NodeRef prevref;
  vertex_t u, v;
  
  for (const osmium::NodeRef& actref : wayNodes)
    {
    std::pair<idvertex_map_t::iterator, bool> p = idvertex_map.emplace(actref.positive_ref(), vertex_t());
    if (p.second) {
    v = boost::add_vertex(actref.positive_ref(), g);
    vertexloc_map.emplace(v, actref.location());
    p.first->second = v;
    }
    else {
    v = idvertex_map[actref.positive_ref()];
    }
    if(prevref.positive_ref()) {
    osmium::geom::Coordinates cord(locations.get(actref.positive_ref()));
    osmium::geom::Coordinates prev_cord(locations.get(prevref.positive_ref()));
    double w = osmium::geom::haversine::distance(cord,prev_cord);
    boost::add_edge(u, v, w, g);
    }
    prevref = actref;
    u = v;
    }
  }
  
  path_t getPath(const osmium::Location& start, const osmium::Location& end)
  {
    if(!path.empty())
    {
      path.clear();
    }
  NearestNode startNode(start, locations);
  osmium::apply(buffer, startNode);
  vertex_t startVertex = idvertex_map[startNode.get()];

  NearestNode endNode(end, locations);
  osmium::apply(buffer, endNode);
  vertex_t endVertex = idvertex_map[endNode.get()];
  
  std::vector<double> distances(boost::num_vertices(g));
  std::vector<vertex_t> predecessors(boost::num_vertices(g));
  boost::dijkstra_shortest_paths(g, startVertex, boost::predecessor_map(&predecessors[0]).distance_map(&distances[0]));
  
    if (endVertex != predecessors[endVertex])
    {
      for (vertex_t v = endVertex; v != startVertex; v = predecessors[v])
      {
	path.push_back(vertexloc_map[v]);
      }
      path.push_back(vertexloc_map[startVertex]);

    std::reverse(path.begin(), path.end());
    }
    else
    {
      std::cout << "Route not found\n";
    }
    return path;
  }
};
