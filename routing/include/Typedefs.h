#ifndef TYPEDEF_H
#define TYPEDEF_H

#include <osmium/index/map/vector.hpp>
#include <osmium/handler/node_locations_for_ways.hpp>

#include <boost/graph/properties.hpp>
#include <boost/graph/graph_traits.hpp>
#include <boost/graph/adjacency_list.hpp>

#include <map>
#include <vector>

typedef osmium::index::map::VectorBasedSparseMap <osmium::unsigned_object_id_type,osmium::Location, std::vector > locations_t;
typedef osmium::handler::NodeLocationsForWays<locations_t> location_handler_t;

typedef boost::property<boost::edge_weight_t, double> weight_property;
typedef boost::property<boost::vertex_name_t, osmium::unsigned_object_id_type> name_property;
typedef boost::adjacency_list <boost::listS, boost::vecS, boost::undirectedS,
        name_property, weight_property> graph_t;
	
typedef boost::graph_traits <graph_t>::vertex_descriptor vertex_t;
typedef std::map<osmium::unsigned_object_id_type, vertex_t> idvertex_map_t;
typedef std::map<vertex_t, osmium::Location> vertexloc_map_t;
typedef std::vector<osmium::Location> path_t;

#endif