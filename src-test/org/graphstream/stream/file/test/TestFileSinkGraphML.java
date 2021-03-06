/*
 * Copyright 2006 - 2016
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.stream.file.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;
import java.io.IOException;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.stream.file.FileSinkGraphML;
import org.graphstream.stream.file.FileSourceGraphML;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TestFileSinkGraphML extends TestFileSinkBase {
    @Override
    protected String aTemporaryGraphFileName() {
        return "foo.graphml";
    }

    @Before
    @Override
    public void setup() {
        input = new FileSourceGraphML();
        output = new FileSinkGraphML();
    }
    
    @Test
    public void test_XmlContent() {
        createXmlContent();
        
        try  {
            output.writeAll(outGraph, new FileOutputStream(aTemporaryGraphFileName()));
            input.addSink(inGraph);
            input.readAll(aTemporaryGraphFileName());
            removeFile(aTemporaryGraphFileName());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue("Should not happen !", false);
        }
        
        assertXmlContent();
    }
    
    protected void createXmlContent() {
        Node x = outGraph.addNode("X");
        Node y = outGraph.addNode("Y");
        Edge xy = outGraph.addEdge("XY", "X", "Y", true);
        
        x.addAttribute("<this>", "<should>");
        y.addAttribute("<break>", "<xml>");
        xy.addAttribute("&lt; also &gt;", "&lt; there is already escaped stuff &gt;");
    }
    
    protected void assertXmlContent() {
        Node x = inGraph.getNode("X");
        Node y = inGraph.getNode("Y");
        Edge xy = inGraph.getEdge("XY");
        
        assertEquals("<should>", x.getAttribute("<this>"));
        assertEquals("<xml>", y.getAttribute("<break>"));
        assertEquals("&lt; there is already escaped stuff &gt;", xy.getAttribute("&lt; also &gt;"));
    }
    
    @Test
    @Ignore
    @Override
    public void test_UndirectedTriangle_ByEvent() { // Not supported
        super.test_UndirectedTriangle_ByEvent();
    }
    
    @Test
    @Ignore
    @Override
    public void test_Dynamic() { // Not supported
        super.test_Dynamic();
    }
    
    @Override
    protected void testAttributedTriangle() {
        assertEquals(3, inGraph.getNodeCount());
        assertEquals(3, inGraph.getEdgeCount());

        Node A = inGraph.getNode("A");
        Node B = inGraph.getNode("B");
        Node C = inGraph.getNode("C");

        assertNotNull(A);
        assertNotNull(B);
        assertNotNull(C);

//        assertEquals(1.0, ((Number) inGraph.getAttribute("a")).doubleValue(), 1E-12); Not supported by this format
//        assertEquals("foo", inGraph.getAttribute("b"));

        assertEquals(1.0, ((Number) A.getAttribute("a")).doubleValue(), 1E-12);
        assertEquals("foo", B.getAttribute("b"));
        assertEquals("bar", C.getAttribute("c"));
    }
}
