/*
 * Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package javafx.scene.control;

import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.scene.control.infrastructure.KeyEventFirer;
import com.sun.javafx.scene.control.infrastructure.StageLoader;
import com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
import com.sun.javafx.scene.control.skin.TextFieldSkin;
import com.sun.javafx.scene.control.skin.VirtualScrollBar;
import com.sun.javafx.scene.control.test.Employee;
import com.sun.javafx.scene.control.test.Person;
import com.sun.javafx.scene.control.test.RT_22463_Person;
import com.sun.javafx.tk.Toolkit;

import java.util.*;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import static com.sun.javafx.scene.control.infrastructure.ControlTestUtils.assertStyleClassContains;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TreeViewTest {
    private TreeView<String> treeView;
    private MultipleSelectionModel<TreeItem<String>> sm;
    private FocusModel<TreeItem<String>> fm;
    
    // sample data #1
    private TreeItem<String> root;
    private TreeItem<String> child1;
    private TreeItem<String> child2;
    private TreeItem<String> child3;
    
    // sample data #1
    private TreeItem<String> myCompanyRootNode;
        private TreeItem<String> salesDepartment;
            private TreeItem<String> ethanWilliams;
            private TreeItem<String> emmaJones;
            private TreeItem<String> michaelBrown;
            private TreeItem<String> annaBlack;
            private TreeItem<String> rodgerYork;
            private TreeItem<String> susanCollins;

        private TreeItem<String> itSupport;
            private TreeItem<String> mikeGraham;
            private TreeItem<String> judyMayer;
            private TreeItem<String> gregorySmith;
            
    private String debug() {
        StringBuilder sb = new StringBuilder("Selected Indices: [");
        
        List<Integer> indices = sm.getSelectedIndices();
        for (Integer index : indices) {
            sb.append(index);
            sb.append(", ");
        }
        
        sb.append("] \nFocus: " + fm.getFocusedIndex());
//        sb.append(" \nAnchor: " + getAnchor());
        return sb.toString();
    }
    
    @Before public void setup() {
        treeView = new TreeView<String>();
        sm = treeView.getSelectionModel();
        fm = treeView.getFocusModel();
        
        // build sample data #2, even though it may not be used...
        myCompanyRootNode = new TreeItem<String>("MyCompany Human Resources");
        salesDepartment = new TreeItem<String>("Sales Department");
            ethanWilliams = new TreeItem<String>("Ethan Williams");
            emmaJones = new TreeItem<String>("Emma Jones");
            michaelBrown = new TreeItem<String>("Michael Brown");
            annaBlack = new TreeItem<String>("Anna Black");
            rodgerYork = new TreeItem<String>("Rodger York");
            susanCollins = new TreeItem<String>("Susan Collins");

        itSupport = new TreeItem<String>("IT Support");
            mikeGraham = new TreeItem<String>("Mike Graham");
            judyMayer = new TreeItem<String>("Judy Mayer");
            gregorySmith = new TreeItem<String>("Gregory Smith");
            
        myCompanyRootNode.getChildren().setAll(
            salesDepartment,
            itSupport
        );
        salesDepartment.getChildren().setAll(
            ethanWilliams,
            emmaJones,
            michaelBrown, 
            annaBlack,
            rodgerYork,
            susanCollins
        );
        itSupport.getChildren().setAll(
            mikeGraham,
            judyMayer,
            gregorySmith
        );
    }

    private void installChildren() {
        root = new TreeItem<String>("Root");
        child1 = new TreeItem<String>("Child 1");
        child2 = new TreeItem<String>("Child 2");
        child3 = new TreeItem<String>("Child 3");
        root.setExpanded(true);
        root.getChildren().setAll(child1, child2, child3);
        treeView.setRoot(root);
    }
    
    @Test public void ensureCorrectInitialState() {
        installChildren();
        assertEquals(0, treeView.getRow(root));
        assertEquals(1, treeView.getRow(child1));
        assertEquals(2, treeView.getRow(child2));
        assertEquals(3, treeView.getRow(child3));
    }

    /*********************************************************************
     * Tests for the constructors                                        *
     ********************************************************************/
    
    @Test public void noArgConstructorSetsTheStyleClass() {
        assertStyleClassContains(treeView, "tree-view");
    }

    @Test public void noArgConstructorSetsNonNullSelectionModel() {
        assertNotNull(treeView.getSelectionModel());
    }

    @Test public void noArgConstructorSetsNullItems() {
        assertNull(treeView.getRoot());
    }

    @Test public void noArgConstructor_selectedItemIsNull() {
        assertNull(treeView.getSelectionModel().getSelectedItem());
    }

    @Test public void noArgConstructor_selectedIndexIsNegativeOne() {
        assertEquals(-1, treeView.getSelectionModel().getSelectedIndex());
    }

    @Test public void singleArgConstructorSetsTheStyleClass() {
        final TreeView<String> b2 = new TreeView<>(new TreeItem<>("Hi"));
        assertStyleClassContains(b2, "tree-view");
    }

    @Test public void singleArgConstructorSetsNonNullSelectionModel() {
        final TreeView<String> b2 = new TreeView<>(new TreeItem<>("Hi"));
        assertNotNull(b2.getSelectionModel());
    }

    @Test public void singleArgConstructorAllowsNullItems() {
        final TreeView<String> b2 = new TreeView<>(null);
        assertNull(b2.getRoot());
    }

    @Test public void singleArgConstructor_selectedItemIsNotNull() {
        TreeItem<String> hiItem = new TreeItem<>("Hi");
        final TreeView<String> b2 = new TreeView<>(hiItem);
        assertNull(b2.getSelectionModel().getSelectedItem());
    }

    @Test public void singleArgConstructor_selectedIndexIsZero() {
        final TreeView<String> b2 = new TreeView<>(new TreeItem<>("Hi"));
        assertEquals(-1, b2.getSelectionModel().getSelectedIndex());
    }

    /*********************************************************************
     * Tests for selection model                                         *
     ********************************************************************/

    @Test public void selectionModelCanBeNull() {
        treeView.setSelectionModel(null);
        assertNull(treeView.getSelectionModel());
    }

    @Test public void selectionModelCanBeBound() {
        MultipleSelectionModel<TreeItem<String>> sm = new TreeView.TreeViewBitSetSelectionModel<String>(treeView);
        ObjectProperty<MultipleSelectionModel<TreeItem<String>>> other = new SimpleObjectProperty<MultipleSelectionModel<TreeItem<String>>>(sm);
        treeView.selectionModelProperty().bind(other);
        assertSame(sm, treeView.getSelectionModel());
    }

    @Test public void selectionModelCanBeChanged() {
        MultipleSelectionModel<TreeItem<String>> sm = new TreeView.TreeViewBitSetSelectionModel<String>(treeView);
        treeView.setSelectionModel(sm);
        assertSame(sm, treeView.getSelectionModel());
    }

    @Test public void canSetSelectedItemToAnItemEvenWhenThereAreNoItems() {
        TreeItem<String> element = new TreeItem<String>("I AM A CRAZY RANDOM STRING");
        treeView.getSelectionModel().select(element);
        assertEquals(-1, treeView.getSelectionModel().getSelectedIndex());
        assertSame(element, treeView.getSelectionModel().getSelectedItem());
    }

    @Test public void canSetSelectedItemToAnItemNotInTheDataModel() {
        installChildren();
        TreeItem<String> element = new TreeItem<String>("I AM A CRAZY RANDOM STRING");
        treeView.getSelectionModel().select(element);
        assertEquals(-1, treeView.getSelectionModel().getSelectedIndex());
        assertSame(element, treeView.getSelectionModel().getSelectedItem());
    }

    @Test public void settingTheSelectedItemToAnItemInItemsResultsInTheCorrectSelectedIndex() {
        installChildren();
        treeView.getSelectionModel().select(child1);
        assertEquals(1, treeView.getSelectionModel().getSelectedIndex());
        assertSame(child1, treeView.getSelectionModel().getSelectedItem());
    }

    @Ignore("Not yet supported")
    @Test public void settingTheSelectedItemToANonexistantItemAndThenSettingItemsWhichContainsItResultsInCorrectSelectedIndex() {
        treeView.getSelectionModel().select(child1);
        installChildren();
        assertEquals(1, treeView.getSelectionModel().getSelectedIndex());
        assertSame(child1, treeView.getSelectionModel().getSelectedItem());
    }
    
    @Ignore("Not yet supported")
    @Test public void ensureSelectionClearsWhenAllItemsAreRemoved_selectIndex0() {
        installChildren();
        treeView.getSelectionModel().select(0);
        treeView.setRoot(null);
        assertEquals(-1, treeView.getSelectionModel().getSelectedIndex());
        assertEquals(null, treeView.getSelectionModel().getSelectedItem());
    }
    
    @Ignore("Not yet supported")
    @Test public void ensureSelectionClearsWhenAllItemsAreRemoved_selectIndex2() {
        installChildren();
        treeView.getSelectionModel().select(2);
        treeView.setRoot(null);
        assertEquals(-1, treeView.getSelectionModel().getSelectedIndex());
        assertEquals(null, treeView.getSelectionModel().getSelectedItem());
    }
    
    @Ignore("Not yet supported")
    @Test public void ensureSelectedItemRemainsAccurateWhenItemsAreCleared() {
        installChildren();
        treeView.getSelectionModel().select(2);
        treeView.setRoot(null);
        assertNull(treeView.getSelectionModel().getSelectedItem());
        assertEquals(-1, treeView.getSelectionModel().getSelectedIndex());
        
        TreeItem<String> newRoot = new TreeItem<String>("New Root");
        TreeItem<String> newChild1 = new TreeItem<String>("New Child 1");
        TreeItem<String> newChild2 = new TreeItem<String>("New Child 2");
        TreeItem<String> newChild3 = new TreeItem<String>("New Child 3");
        newRoot.setExpanded(true);
        newRoot.getChildren().setAll(newChild1, newChild2, newChild3);
        treeView.setRoot(root);
        
        treeView.getSelectionModel().select(2);
        assertEquals(newChild2, treeView.getSelectionModel().getSelectedItem());
    }
    
    @Test public void ensureSelectionIsCorrectWhenItemsChange() {
        installChildren();
        treeView.getSelectionModel().select(0);
        assertEquals(root, treeView.getSelectionModel().getSelectedItem());

        TreeItem newRoot = new TreeItem<>("New Root");
        treeView.setRoot(newRoot);
        assertEquals(-1, treeView.getSelectionModel().getSelectedIndex());
        assertNull(treeView.getSelectionModel().getSelectedItem());
    }
    
    @Test public void ensureSelectionRemainsOnBranchWhenExpanded() {
        installChildren();
        root.setExpanded(false);
        treeView.getSelectionModel().select(0);
        assertTrue(treeView.getSelectionModel().isSelected(0));
        root.setExpanded(true);
        assertTrue(treeView.getSelectionModel().isSelected(0));
        assertTrue(treeView.getSelectionModel().getSelectedItems().contains(root));
    }
    
    /*********************************************************************
     * Tests for misc                                                    *
     ********************************************************************/
    @Test public void ensureRootIndexIsZeroWhenRootIsShowing() {
        installChildren();
        assertEquals(0, treeView.getRow(root));
    }
    
    @Test public void ensureRootIndexIsNegativeOneWhenRootIsNotShowing() {
        installChildren();
        treeView.setShowRoot(false);
        assertEquals(-1, treeView.getRow(root));
    }
    
    @Test public void ensureCorrectIndexWhenRootTreeItemHasParent() {
        installChildren();
        treeView.setRoot(child1);
        assertEquals(-1, treeView.getRow(root));
        assertEquals(0, treeView.getRow(child1));
        assertEquals(1, treeView.getRow(child2));
        assertEquals(2, treeView.getRow(child3));
    }
    
    @Test public void ensureCorrectIndexWhenRootTreeItemHasParentAndRootIsNotShowing() {
        installChildren();
        treeView.setRoot(child1);
        treeView.setShowRoot(false);
        
        // despite the fact there are children in this tree, in reality none are
        // visible as the root node has no children (only siblings), and the
        // root node is not visible.
        assertEquals(0, treeView.getExpandedItemCount());
        
        assertEquals(-1, treeView.getRow(root));
        assertEquals(-1, treeView.getRow(child1));
        assertEquals(-1, treeView.getRow(child2));
        assertEquals(-1, treeView.getRow(child3));
    }
    
    @Test public void ensureCorrectIndexWhenRootTreeItemIsCollapsed() {
        installChildren();
        root.setExpanded(false);
        assertEquals(0, treeView.getRow(root));
        
        // note that the indices are still positive, representing what the values
        // would be if this row is visible
        assertEquals(1, treeView.getRow(child1));
        assertEquals(2, treeView.getRow(child2));
        assertEquals(3, treeView.getRow(child3));
    }
    
    @Test public void removingLastTest() {
        TreeView tree_view = new TreeView();
        MultipleSelectionModel sm = tree_view.getSelectionModel();
        TreeItem<String> tree_model = new TreeItem<String>("Root");
        TreeItem node = new TreeItem("Data item");
        tree_model.getChildren().add(node);
        tree_view.setRoot(tree_model);
        tree_model.setExpanded(true);
        // select the 'Data item' in the selection model
        sm.select(tree_model.getChildren().get(0));
        // remove the 'Data item' from the root node
        tree_model.getChildren().remove(sm.getSelectedItem());

        // Previously the selection was cleared, but this was changed to instead
        // move the selection upwards.
        // assert the there are no selected items any longer
        // assertTrue("items: " + sm.getSelectedItem(), sm.getSelectedItems().isEmpty());
        assertEquals(tree_model, sm.getSelectedItem());
    }
    
    /*********************************************************************
     * Tests from bug reports                                            *
     ********************************************************************/  
    @Ignore @Test public void test_rt17112() {
        TreeItem<String> root1 = new TreeItem<String>("Root");
        root1.setExpanded(true);
        addChildren(root1, "child");
        for (TreeItem child : root1.getChildren()) {
            addChildren(child, (String)child.getValue());
            child.setExpanded(true);
        }

        final TreeView treeView1 = new TreeView();
        final MultipleSelectionModel sm = treeView1.getSelectionModel();
        sm.setSelectionMode(SelectionMode.MULTIPLE);
        treeView1.setRoot(root1);
        
        final TreeItem<String> rt17112_child1 = root1.getChildren().get(1);
        final TreeItem<String> rt17112_child1_0 = rt17112_child1.getChildren().get(0);
        final TreeItem<String> rt17112_child2 = root1.getChildren().get(2);
        
        sm.getSelectedItems().addListener(new InvalidationListener() {
            int count = 0;
            @Override public void invalidated(Observable observable) {
                if (count == 0) {
                    assertEquals(rt17112_child1_0, sm.getSelectedItem());
                    assertEquals(1, sm.getSelectedIndices().size());
                    assertEquals(6, sm.getSelectedIndex());
                    assertTrue(treeView1.getFocusModel().isFocused(6));
                } else if (count == 1) {
                    assertEquals(rt17112_child1, sm.getSelectedItem());
                    assertFalse(sm.getSelectedItems().contains(rt17112_child2));
                    assertEquals(1, sm.getSelectedIndices().size());
                    assertTrue(treeView1.getFocusModel().isFocused(5));
                }
                count++;
            }
        });
        
        // this triggers the first callback above, so that count == 0
        sm.select(rt17112_child1_0);

        // this triggers the second callback above, so that count == 1
        rt17112_child1.setExpanded(false);
    }
    private void addChildren(TreeItem parent, String name) {
        for (int i=0; i<3; i++) {
            TreeItem<String> ti = new TreeItem<String>(name+"-"+i);
            parent.getChildren().add(ti);
        }
    }
    
    @Test public void test_rt17522_focusShouldMoveWhenItemAddedAtFocusIndex() {
        installChildren();
        FocusModel fm = treeView.getFocusModel();
        fm.focus(1);    // focus on child1
        assertTrue(fm.isFocused(1));
        assertEquals(child1, fm.getFocusedItem());
        
        TreeItem child0 = new TreeItem("child0");
        root.getChildren().add(0, child0);  // 0th index == position of child1 in root
        
        assertEquals(child1, fm.getFocusedItem());
        assertTrue(fm.isFocused(2));
    }
    
    @Test public void test_rt17522_focusShouldMoveWhenItemAddedBeforeFocusIndex() {
        installChildren();
        FocusModel fm = treeView.getFocusModel();
        fm.focus(1);    // focus on child1
        assertTrue(fm.isFocused(1));
        
        TreeItem child0 = new TreeItem("child0");
        root.getChildren().add(0, child0);
        assertTrue("Focused index: " + fm.getFocusedIndex(), fm.isFocused(2));
    }
    
    @Test public void test_rt17522_focusShouldNotMoveWhenItemAddedAfterFocusIndex() {
        installChildren();
        FocusModel fm = treeView.getFocusModel();
        fm.focus(1);    // focus on child1
        assertTrue(fm.isFocused(1));
        
        TreeItem child4 = new TreeItem("child4");
        root.getChildren().add(3, child4);
        assertTrue("Focused index: " + fm.getFocusedIndex(), fm.isFocused(1));
    }
    
    @Test public void test_rt17522_focusShouldBeResetWhenFocusedItemIsRemoved() {
        installChildren();
        FocusModel fm = treeView.getFocusModel();
        fm.focus(1);
        assertTrue(fm.isFocused(1));
        
        root.getChildren().remove(child1);
        assertEquals(-1, fm.getFocusedIndex());
        assertNull(fm.getFocusedItem());
    }
    
    @Test public void test_rt17522_focusShouldMoveWhenItemRemovedBeforeFocusIndex() {
        installChildren();
        FocusModel fm = treeView.getFocusModel();
        fm.focus(2);
        assertTrue(fm.isFocused(2));
        
        root.getChildren().remove(child1);
        assertTrue(fm.isFocused(1));
        assertEquals(child2, fm.getFocusedItem());
    }

//    This test fails as, in TreeView FocusModel, we do not know the index of the
//    removed tree items, which means we don't know whether they existed before
//    or after the focused item.
//    @Test public void test_rt17522_focusShouldNotMoveWhenItemRemovedAfterFocusIndex() {
//        installChildren();
//        FocusModel fm = treeView.getFocusModel();
//        fm.focus(1);
//        assertTrue(fm.isFocused(1));
//        
//        root.getChildren().remove(child3);
//        assertTrue("Focused index: " + fm.getFocusedIndex(), fm.isFocused(1));
//        assertEquals(child1, fm.getFocusedItem());
//    }
    
    @Test public void test_rt18385() {
        installChildren();
//        table.getItems().addAll("row1", "row2", "row3");
        treeView.getSelectionModel().select(1);
        treeView.getRoot().getChildren().add(new TreeItem("Another Row"));
        assertEquals(1, treeView.getSelectionModel().getSelectedIndices().size());
        assertEquals(1, treeView.getSelectionModel().getSelectedItems().size());
    }
    
    @Test public void test_rt18339_onlyEditWhenTreeViewIsEditable_editableIsFalse() {
        treeView.setEditable(false);
        treeView.edit(root);
        assertEquals(null, treeView.getEditingItem());
    }
    
    @Test public void test_rt18339_onlyEditWhenTreeViewIsEditable_editableIsTrue() {
        treeView.setEditable(true);
        treeView.edit(root);
        assertEquals(root, treeView.getEditingItem());
    }
    
    @Test public void test_rt14451() {
        installChildren();
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        treeView.getSelectionModel().selectRange(0, 2); // select from 0 (inclusive) to 2 (exclusive)
        assertEquals(2, treeView.getSelectionModel().getSelectedIndices().size());
    }
    
    @Test public void test_rt21586() {
        installChildren();
        treeView.getSelectionModel().select(1);
        assertEquals(1, treeView.getSelectionModel().getSelectedIndex());
        assertEquals(child1, treeView.getSelectionModel().getSelectedItem());
        
        TreeItem root = new TreeItem<>("New Root");
        TreeItem child1 = new TreeItem<>("New Child 1");
        TreeItem child2 = new TreeItem<>("New Child 2");
        TreeItem child3 = new TreeItem<>("New Child 3");
        root.setExpanded(true);
        root.getChildren().setAll(child1, child2, child3);
        treeView.setRoot(root);
        assertEquals(-1, treeView.getSelectionModel().getSelectedIndex());
        assertNull(treeView.getSelectionModel().getSelectedItem());
    }
    
    @Test public void test_rt27181() {
        myCompanyRootNode.setExpanded(true);
        treeView.setRoot(myCompanyRootNode);
        
        // start test
        salesDepartment.setExpanded(true);
        treeView.getSelectionModel().select(salesDepartment);
        
        assertEquals(1, treeView.getFocusModel().getFocusedIndex());
        itSupport.setExpanded(true);
        assertEquals(1, treeView.getFocusModel().getFocusedIndex());
    }
    
    @Test public void test_rt27185() {
        myCompanyRootNode.setExpanded(true);
        treeView.setRoot(myCompanyRootNode);
        
        // start test
        itSupport.setExpanded(true);
        treeView.getSelectionModel().select(mikeGraham);
        
        assertEquals(mikeGraham, treeView.getFocusModel().getFocusedItem());
        salesDepartment.setExpanded(true);
        assertEquals(mikeGraham, treeView.getFocusModel().getFocusedItem());
    }
    
    @Ignore("Bug hasn't been fixed yet")
    @Test public void test_rt28114() {
        myCompanyRootNode.setExpanded(true);
        treeView.setRoot(myCompanyRootNode);
        
        // start test
        itSupport.setExpanded(true);
        treeView.getSelectionModel().select(itSupport);
        assertEquals(itSupport, treeView.getFocusModel().getFocusedItem());
        assertEquals(itSupport, treeView.getSelectionModel().getSelectedItem());
        assertTrue(! itSupport.isLeaf());
        assertTrue(itSupport.isExpanded());
        
        itSupport.getChildren().remove(mikeGraham);
        assertEquals(itSupport, treeView.getFocusModel().getFocusedItem());
        assertEquals(itSupport, treeView.getSelectionModel().getSelectedItem());
        assertTrue(itSupport.isLeaf());
        assertTrue(!itSupport.isExpanded());
    }
    
    @Test public void test_rt27820_1() {
        TreeItem root = new TreeItem("root");
        root.setExpanded(true);
        TreeItem child = new TreeItem("child");
        root.getChildren().add(child);
        treeView.setRoot(root);
        
        treeView.getSelectionModel().select(0);
        assertEquals(1, treeView.getSelectionModel().getSelectedItems().size());
        assertEquals(root, treeView.getSelectionModel().getSelectedItem());
        
        treeView.setRoot(null);
        assertEquals(0, treeView.getSelectionModel().getSelectedItems().size());
        assertNull(treeView.getSelectionModel().getSelectedItem());
    }
    
    @Test public void test_rt27820_2() {
        TreeItem root = new TreeItem("root");
        root.setExpanded(true);
        TreeItem child = new TreeItem("child");
        root.getChildren().add(child);
        treeView.setRoot(root);
        
        treeView.getSelectionModel().select(1);
        assertEquals(1, treeView.getSelectionModel().getSelectedItems().size());
        assertEquals(child, treeView.getSelectionModel().getSelectedItem());
        
        treeView.setRoot(null);
        assertEquals(0, treeView.getSelectionModel().getSelectedItems().size());
        assertNull(treeView.getSelectionModel().getSelectedItem());
    }
    
    @Test public void test_rt28390() {
        // There should be no NPE when a TreeView is shown and the disclosure
        // node is null in a TreeCell
        TreeItem root = new TreeItem("root");
        treeView.setRoot(root);
        
        // install a custom cell factory that forces the disclosure node to be
        // null (because by default a null disclosure node will be replaced by
        // a non-null one).
        treeView.setCellFactory(new Callback() {
            @Override public Object call(Object p) {
                TreeCell treeCell = new TreeCell() {
                    {
                        disclosureNodeProperty().addListener((ov, t, t1) -> {
                            setDisclosureNode(null);
                        });
                    }
                    
                    @Override protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item == null ? "" : item.toString());
                    }
                };
                treeCell.setDisclosureNode(null);
                return treeCell;
            }
        });
        
        try {
            Group group = new Group();
            group.getChildren().setAll(treeView);
            Scene scene = new Scene(group);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (NullPointerException e) {
            System.out.println("A null disclosure node is valid, so we shouldn't have an NPE here.");
            e.printStackTrace();
            assertTrue(false);
        }
    }
    
    @Test public void test_rt28534() {
        TreeItem root = new TreeItem("root");
        root.getChildren().setAll(
                new TreeItem(new Person("Jacob", "Smith", "jacob.smith@example.com")),
                new TreeItem(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
                new TreeItem(new Person("Ethan", "Williams", "ethan.williams@example.com")),
                new TreeItem(new Person("Emma", "Jones", "emma.jones@example.com")),
                new TreeItem(new Person("Michael", "Brown", "michael.brown@example.com")));
        root.setExpanded(true);
        
        TreeView<Person> tree = new TreeView<Person>(root);
        
        VirtualFlowTestUtils.assertRowsNotEmpty(tree, 0, 6); // rows 0 - 6 should be filled
        VirtualFlowTestUtils.assertRowsEmpty(tree, 6, -1); // rows 6+ should be empty
        
        // now we replace the data and expect the cells that have no data
        // to be empty
        root.getChildren().setAll(
                new TreeItem(new Person("*_*Emma", "Jones", "emma.jones@example.com")),
                new TreeItem(new Person("_Michael", "Brown", "michael.brown@example.com")));
        
        VirtualFlowTestUtils.assertRowsNotEmpty(tree, 0, 3); // rows 0 - 3 should be filled
        VirtualFlowTestUtils.assertRowsEmpty(tree, 3, -1); // rows 3+ should be empty
    }

    @Test public void test_rt28556() {
        List<Employee> employees = Arrays.<Employee>asList(
            new Employee("Ethan Williams", "Sales Department"),
            new Employee("Emma Jones", "Sales Department"),
            new Employee("Michael Brown", "Sales Department"),
            new Employee("Anna Black", "Sales Department"),
            new Employee("Rodger York", "Sales Department"),
            new Employee("Susan Collins", "Sales Department"),
            new Employee("Mike Graham", "IT Support"),
            new Employee("Judy Mayer", "IT Support"),
            new Employee("Gregory Smith", "IT Support"),
            new Employee("Jacob Smith", "Accounts Department"),
            new Employee("Isabella Johnson", "Accounts Department"));
    
        TreeItem<String> rootNode = new TreeItem<String>("MyCompany Human Resources");
        rootNode.setExpanded(true);
        
        List<TreeItem<String>> nodeList = FXCollections.observableArrayList();
        for (Employee employee : employees) {
            nodeList.add(new TreeItem<String>(employee.getName()));
        }
        rootNode.getChildren().setAll(nodeList);

        TreeView<String> treeView = new TreeView<String>(rootNode);
        
        final double indent = PlatformImpl.isCaspian() ? 31 : 
                        PlatformImpl.isModena()  ? 35 :
                        0;
        
        // ensure all children of the root node have the correct indentation 
        // before the sort occurs
        VirtualFlowTestUtils.assertLayoutX(treeView, 1, 11, indent);
        for (TreeItem<String> children : rootNode.getChildren()) {
            assertEquals(rootNode, children.getParent());
        }
        
        // run sort
        Collections.sort(rootNode.getChildren(), (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
        
        // ensure the same indentation exists after the sort (which is where the
        // bug is - it drops down to 21.0px indentation when it shouldn't).
        VirtualFlowTestUtils.assertLayoutX(treeView, 1, 11, indent);
        for (TreeItem<String> children : rootNode.getChildren()) {
            assertEquals(rootNode, children.getParent());
        }
    }
    
    @Test public void test_rt22463() {
        RT_22463_Person rootPerson = new RT_22463_Person();
        rootPerson.setName("Root");
        TreeItem<RT_22463_Person> root = new TreeItem<RT_22463_Person>(rootPerson);
        root.setExpanded(true);
        
        final TreeView<RT_22463_Person> tree = new TreeView<RT_22463_Person>();
        tree.setRoot(root);
        
        // before the change things display fine
        RT_22463_Person p1 = new RT_22463_Person();
        p1.setId(1l);
        p1.setName("name1");
        RT_22463_Person p2 = new RT_22463_Person();
        p2.setId(2l);
        p2.setName("name2");
        root.getChildren().addAll(
                new TreeItem<RT_22463_Person>(p1), 
                new TreeItem<RT_22463_Person>(p2));
        VirtualFlowTestUtils.assertCellTextEquals(tree, 1, "name1");
        VirtualFlowTestUtils.assertCellTextEquals(tree, 2, "name2");
        
        // now we change the persons but they are still equal as the ID's don't
        // change - but the items list is cleared so the cells should update
        RT_22463_Person new_p1 = new RT_22463_Person();
        new_p1.setId(1l);
        new_p1.setName("updated name1");
        RT_22463_Person new_p2 = new RT_22463_Person();
        new_p2.setId(2l);
        new_p2.setName("updated name2");
        root.getChildren().clear();
        root.getChildren().setAll(
                new TreeItem<RT_22463_Person>(new_p1), 
                new TreeItem<RT_22463_Person>(new_p2));
        VirtualFlowTestUtils.assertCellTextEquals(tree, 1, "updated name1");
        VirtualFlowTestUtils.assertCellTextEquals(tree, 2, "updated name2");
    }
    
    @Test public void test_rt28637() {
        TreeItem<String> s1, s2, s3, s4;
        ObservableList<TreeItem<String>> items = FXCollections.observableArrayList(
                s1 = new TreeItem<String>("String1"), 
                s2 = new TreeItem<String>("String2"), 
                s3 = new TreeItem<String>("String3"), 
                s4 = new TreeItem<String>("String4"));
        
        final TreeView<String> treeView = new TreeView<String>();
        
        TreeItem<String> root = new TreeItem<String>("Root");
        root.setExpanded(true);
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        root.getChildren().addAll(items);
        
        treeView.getSelectionModel().select(0);
        assertEquals((Object)s1, treeView.getSelectionModel().getSelectedItem());
        assertEquals((Object)s1, treeView.getSelectionModel().getSelectedItems().get(0));
        assertEquals(0, treeView.getSelectionModel().getSelectedIndex());
        
        root.getChildren().remove(treeView.getSelectionModel().getSelectedItem());
        assertEquals((Object)s2, treeView.getSelectionModel().getSelectedItem());
        assertEquals((Object)s2, treeView.getSelectionModel().getSelectedItems().get(0));
        assertEquals(0, treeView.getSelectionModel().getSelectedIndex());
    }
    
    @Ignore("Test passes from within IDE but not when run from command line. Needs more investigation.")
    @Test public void test_rt28678() {
        TreeItem<String> s1, s2, s3, s4;
        ObservableList<TreeItem<String>> items = FXCollections.observableArrayList(
                s1 = new TreeItem<String>("String1"), 
                s2 = new TreeItem<String>("String2"), 
                s3 = new TreeItem<String>("String3"), 
                s4 = new TreeItem<String>("String4"));
        
        final TreeView<String> treeView = new TreeView<String>();
        
        TreeItem<String> root = new TreeItem<String>("Root");
        root.setExpanded(true);
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        root.getChildren().addAll(items);
        
        Node graphic = new Circle(6, Color.RED);
        
        assertNull(s2.getGraphic());
        TreeCell s2Cell = (TreeCell) VirtualFlowTestUtils.getCell(treeView, 1);
        assertNull(s2Cell.getGraphic());
        
        s2.setGraphic(graphic);
        Toolkit.getToolkit().firePulse();
                
        assertEquals(graphic, s2.getGraphic());
        assertEquals(graphic, s2Cell.getGraphic());
    }
    
    @Test public void test_rt29390() {
        ObservableList<TreeItem<Person>> persons = FXCollections.observableArrayList(
                new TreeItem<Person>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
                new TreeItem<Person>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
                new TreeItem<Person>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
                new TreeItem<Person>(new Person("Emma", "Jones", "emma.jones@example.com")),
                new TreeItem<Person>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
                new TreeItem<Person>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
                new TreeItem<Person>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
                new TreeItem<Person>(new Person("Emma", "Jones", "emma.jones@example.com")),
                new TreeItem<Person>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
                new TreeItem<Person>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
                new TreeItem<Person>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
                new TreeItem<Person>(new Person("Emma", "Jones", "emma.jones@example.com")),
                new TreeItem<Person>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
                new TreeItem<Person>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
                new TreeItem<Person>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
                new TreeItem<Person>(new Person("Emma", "Jones", "emma.jones@example.com")
        ));
                
        TreeView<Person> treeView = new TreeView<>();
        treeView.setMaxHeight(50);
        treeView.setPrefHeight(50);
        
        TreeItem<Person> root = new TreeItem<Person>(new Person("Root", null, null));
        root.setExpanded(true);
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        root.getChildren().setAll(persons);
        
        Toolkit.getToolkit().firePulse();
        
        // we want the vertical scrollbar
        VirtualScrollBar scrollBar = VirtualFlowTestUtils.getVirtualFlowVerticalScrollbar(treeView);
        
        assertNotNull(scrollBar);
        assertTrue(scrollBar.isVisible());
        assertTrue(scrollBar.getVisibleAmount() > 0.0);
        assertTrue(scrollBar.getVisibleAmount() < 1.0);
        
        // this next test is likely to be brittle, but we'll see...If it is the
        // cause of failure then it can be commented out
        assertEquals(0.125, scrollBar.getVisibleAmount(), 0.0);
    }
    
    @Test public void test_rt27180_collapseBranch_childSelected_singleSelection() {
        sm.setSelectionMode(SelectionMode.SINGLE);
        
        treeView.setRoot(myCompanyRootNode);
        myCompanyRootNode.setExpanded(true);
        salesDepartment.setExpanded(true);
        itSupport.setExpanded(true);
        sm.select(2);                   // ethanWilliams
        assertFalse(sm.isSelected(1));  // salesDepartment
        assertTrue(sm.isSelected(2));   // ethanWilliams
        assertTrue(treeView.getFocusModel().isFocused(2));
        assertEquals(1, sm.getSelectedIndices().size());
        
        // now collapse the salesDepartment, selection should
        // not jump down to the itSupport people
        salesDepartment.setExpanded(false);
        assertTrue(sm.isSelected(1));   // salesDepartment
        assertTrue(treeView.getFocusModel().isFocused(1));
        assertEquals(1, sm.getSelectedIndices().size());
    }
    
    @Test public void test_rt27180_collapseBranch_laterSiblingSelected_singleSelection() {
        sm.setSelectionMode(SelectionMode.SINGLE);
        
        treeView.setRoot(myCompanyRootNode);
        myCompanyRootNode.setExpanded(true);
        salesDepartment.setExpanded(true);
        itSupport.setExpanded(true);
        sm.select(8);                   // itSupport
        assertFalse(sm.isSelected(1));  // salesDepartment
        assertTrue(sm.isSelected(8));   // itSupport
        assertTrue(treeView.getFocusModel().isFocused(8));
        assertEquals(1, sm.getSelectedIndices().size());
        
        salesDepartment.setExpanded(false);
        assertTrue(sm.isSelected(2));   // itSupport
        assertTrue(treeView.getFocusModel().isFocused(2));
        assertEquals(1, sm.getSelectedIndices().size());
    }
    
    @Test public void test_rt27180_collapseBranch_laterSiblingAndChildrenSelected() {
        sm.setSelectionMode(SelectionMode.MULTIPLE);
        
        treeView.setRoot(myCompanyRootNode);
        treeView.getSelectionModel().clearSelection();

        myCompanyRootNode.setExpanded(true);
        salesDepartment.setExpanded(true);
        itSupport.setExpanded(true);
        sm.selectIndices(8, 9, 10);     // itSupport, and two people
        assertFalse(sm.isSelected(1));  // salesDepartment
        assertTrue(sm.isSelected(8));   // itSupport
        assertTrue(sm.isSelected(9));   // mikeGraham
        assertTrue(sm.isSelected(10));  // judyMayer
        assertTrue(treeView.getFocusModel().isFocused(10));
        assertEquals(3, sm.getSelectedIndices().size());
        
        salesDepartment.setExpanded(false);
        assertTrue(sm.isSelected(2));   // itSupport
        assertTrue(sm.isSelected(3));   // mikeGraham
        assertTrue(sm.isSelected(4));   // judyMayer
        assertTrue(treeView.getFocusModel().isFocused(4));
        assertEquals(3, sm.getSelectedIndices().size());
    }
    
    @Test public void test_rt27180_expandBranch_laterSiblingSelected_singleSelection() {
        sm.setSelectionMode(SelectionMode.SINGLE);
        
        treeView.setRoot(myCompanyRootNode);
        myCompanyRootNode.setExpanded(true);
        salesDepartment.setExpanded(false);
        itSupport.setExpanded(true);
        sm.select(2);                   // itSupport
        assertFalse(sm.isSelected(1));  // salesDepartment
        assertTrue(sm.isSelected(2));   // itSupport
        assertTrue(treeView.getFocusModel().isFocused(2));
        assertEquals(1, sm.getSelectedIndices().size());
        
        salesDepartment.setExpanded(true);
        assertTrue(sm.isSelected(8));   // itSupport
        assertTrue(treeView.getFocusModel().isFocused(8));
        assertEquals(1, sm.getSelectedIndices().size());
    }
    
    @Test public void test_rt27180_expandBranch_laterSiblingAndChildrenSelected() {
        sm.setSelectionMode(SelectionMode.MULTIPLE);
        
        treeView.setRoot(myCompanyRootNode);
        treeView.getSelectionModel().clearSelection();

        myCompanyRootNode.setExpanded(true);
        salesDepartment.setExpanded(false);
        itSupport.setExpanded(true);
        sm.selectIndices(2,3,4);     // itSupport, and two people
        assertFalse(sm.isSelected(1));  // salesDepartment
        assertTrue(sm.isSelected(2));   // itSupport
        assertTrue(sm.isSelected(3));   // mikeGraham
        assertTrue(sm.isSelected(4));  // judyMayer
        assertTrue(treeView.getFocusModel().isFocused(4));
        assertEquals(3, sm.getSelectedIndices().size());
        
        salesDepartment.setExpanded(true);
        assertTrue(sm.isSelected(8));   // itSupport
        assertTrue(sm.isSelected(9));   // mikeGraham
        assertTrue(sm.isSelected(10));   // judyMayer
        assertTrue(treeView.getFocusModel().isFocused(10));
        assertEquals(3, sm.getSelectedIndices().size());
    }

    @Test public void test_rt30400() {
        // create a treeview that'll render cells using the check box cell factory
        TreeItem<String> rootItem = new TreeItem<>("root");
        treeView.setRoot(rootItem);
        treeView.setMinHeight(100);
        treeView.setPrefHeight(100);
        treeView.setCellFactory(
                CheckBoxTreeCell.forTreeView(
                        param -> new ReadOnlyBooleanWrapper(true)));

        // because only the first row has data, all other rows should be
        // empty (and not contain check boxes - we just check the first four here)
        VirtualFlowTestUtils.assertRowsNotEmpty(treeView, 0, 1);
        VirtualFlowTestUtils.assertCellNotEmpty(VirtualFlowTestUtils.getCell(treeView, 0));
        VirtualFlowTestUtils.assertCellEmpty(VirtualFlowTestUtils.getCell(treeView, 1));
        VirtualFlowTestUtils.assertCellEmpty(VirtualFlowTestUtils.getCell(treeView, 2));
        VirtualFlowTestUtils.assertCellEmpty(VirtualFlowTestUtils.getCell(treeView, 3));
    }

    @Test public void test_rt31165() {
        installChildren();
        treeView.setEditable(true);
        treeView.setCellFactory(TextFieldTreeCell.forTreeView());

        IndexedCell cell = VirtualFlowTestUtils.getCell(treeView, 1);
        assertEquals(child1.getValue(), cell.getText());
        assertFalse(cell.isEditing());

        treeView.edit(child1);

        assertEquals(child1, treeView.getEditingItem());
        assertTrue(cell.isEditing());

        VirtualFlowTestUtils.getVirtualFlow(treeView).requestLayout();
        Toolkit.getToolkit().firePulse();

        assertEquals(child1, treeView.getEditingItem());
        assertTrue(cell.isEditing());
    }

    @Test public void test_rt31404() {
        installChildren();

        IndexedCell cell = VirtualFlowTestUtils.getCell(treeView, 0);
        assertEquals("Root", cell.getText());

        treeView.setShowRoot(false);
        assertEquals("Child 1", cell.getText());
    }

    @Test public void test_rt31471() {
        installChildren();

        IndexedCell cell = VirtualFlowTestUtils.getCell(treeView, 0);
        assertEquals("Root", cell.getItem());

        treeView.setFixedCellSize(50);

        VirtualFlowTestUtils.getVirtualFlow(treeView).requestLayout();
        Toolkit.getToolkit().firePulse();

        assertEquals("Root", cell.getItem());
        assertEquals(50, cell.getHeight(), 0.00);
    }

    private int rt_31200_count = 0;
    @Test public void test_rt_31200_tableRow() {
        installChildren();
        treeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
            @Override
            public TreeCell<String> call(TreeView<String> param) {
                return new TreeCell<String>() {
                    ImageView view = new ImageView();
                    { setGraphic(view); };

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (getItem() == null ? item == null : getItem().equals(item)) {
                            rt_31200_count++;
                        }
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            view.setImage(null);
                            setText(null);
                        } else {
                            setText(item.toString());
                        }
                    }
                };
            }
        });

        StageLoader sl = new StageLoader(treeView);

        assertEquals(24, rt_31200_count);

        // resize the stage
        sl.getStage().setHeight(250);
        Toolkit.getToolkit().firePulse();
        sl.getStage().setHeight(50);
        Toolkit.getToolkit().firePulse();
        assertEquals(24, rt_31200_count);

        sl.dispose();
    }

    @Test public void test_rt_30484() {
        installChildren();
        treeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
            @Override public TreeCell<String> call(TreeView<String> param) {
                return new TreeCell<String>() {
                    Rectangle graphic = new Rectangle(10, 10, Color.RED);
                    { setGraphic(graphic); };

                    @Override protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            graphic.setVisible(false);
                            setText(null);
                        } else {
                            graphic.setVisible(true);
                            setText(item);
                        }
                    }
                };
            }
        });

        // First two four have content, so the graphic should show.
        // All other rows have no content, so graphic should not show.

        VirtualFlowTestUtils.assertGraphicIsVisible(treeView, 0);
        VirtualFlowTestUtils.assertGraphicIsVisible(treeView, 1);
        VirtualFlowTestUtils.assertGraphicIsVisible(treeView, 2);
        VirtualFlowTestUtils.assertGraphicIsVisible(treeView, 3);
        VirtualFlowTestUtils.assertGraphicIsNotVisible(treeView, 4);
        VirtualFlowTestUtils.assertGraphicIsNotVisible(treeView, 5);
    }

    private int rt_29650_start_count = 0;
    private int rt_29650_commit_count = 0;
    private int rt_29650_cancel_count = 0;
    @Test public void test_rt_29650() {
        installChildren();
        treeView.setOnEditStart(t -> {
            rt_29650_start_count++;
        });
        treeView.setOnEditCommit(t -> {
            rt_29650_commit_count++;
        });
        treeView.setOnEditCancel(t -> {
            rt_29650_cancel_count++;
        });

        treeView.setEditable(true);
        treeView.setCellFactory(TextFieldTreeCell.forTreeView());

        StageLoader sl = new StageLoader(treeView);

        treeView.edit(root);
        TreeCell rootCell = (TreeCell) VirtualFlowTestUtils.getCell(treeView, 0);
        TextField textField = (TextField) rootCell.getGraphic();
        textField.setSkin(new TextFieldSkin(textField));
        textField.setText("Testing!");
        KeyEventFirer keyboard = new KeyEventFirer(textField);
        keyboard.doKeyPress(KeyCode.ENTER);

        assertEquals("Testing!", root.getValue());
        assertEquals(1, rt_29650_start_count);
        assertEquals(1, rt_29650_commit_count);
        assertEquals(0, rt_29650_cancel_count);

        sl.dispose();
    }

    private int rt_33559_count = 0;
    @Test public void test_rt_33559() {
        installChildren();

        treeView.setShowRoot(true);
        final MultipleSelectionModel sm = treeView.getSelectionModel();
        sm.setSelectionMode(SelectionMode.MULTIPLE);
        sm.clearAndSelect(0);

        treeView.getSelectionModel().getSelectedItems().addListener((ListChangeListener) c -> {
            while (c.next()) {
                System.out.println(c);
                rt_33559_count++;
            }
        });

        assertEquals(0, rt_33559_count);
        root.setExpanded(true);
        assertEquals(0, rt_33559_count);
    }

    @Test public void test_rt34103() {
        treeView.setRoot(new TreeItem("Root"));
        treeView.getRoot().setExpanded(true);

        for (int i = 0; i < 4; i++) {
            TreeItem parent = new TreeItem("item - " + i);
            treeView.getRoot().getChildren().add(parent);

            for (int j = 0; j < 4; j++) {
                TreeItem child = new TreeItem("item - " + i + " " + j);
                parent.getChildren().add(child);
            }
        }

        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TreeItem item0 = treeView.getTreeItem(1);
        assertEquals("item - 0", item0.getValue());
        item0.setExpanded(true);

        treeView.getSelectionModel().clearSelection();
        treeView.getSelectionModel().selectIndices(1,2,3);
        assertEquals(3, treeView.getSelectionModel().getSelectedIndices().size());

        item0.setExpanded(false);
        Toolkit.getToolkit().firePulse();
        assertEquals(1, treeView.getSelectionModel().getSelectedIndices().size());
    }

    @Test public void test_rt26718() {
        treeView.setRoot(new TreeItem("Root"));
        treeView.getRoot().setExpanded(true);

        for (int i = 0; i < 4; i++) {
            TreeItem parent = new TreeItem("item - " + i);
            treeView.getRoot().getChildren().add(parent);

            for (int j = 0; j < 4; j++) {
                TreeItem child = new TreeItem("item - " + i + " " + j);
                parent.getChildren().add(child);
            }
        }

        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        final TreeItem item0 = treeView.getTreeItem(1);
        final TreeItem item1 = treeView.getTreeItem(2);

        assertEquals("item - 0", item0.getValue());
        assertEquals("item - 1", item1.getValue());

        item0.setExpanded(true);
        item1.setExpanded(true);
        Toolkit.getToolkit().firePulse();

        treeView.getSelectionModel().selectRange(0, 8);
        assertEquals(8, treeView.getSelectionModel().getSelectedIndices().size());
        assertEquals(7, treeView.getSelectionModel().getSelectedIndex());
        assertEquals(7, treeView.getFocusModel().getFocusedIndex());

        // collapse item0 - but because the selected and focused indices are
        // not children of item 0, they should remain where they are (but of
        // course be shifted up). The bug was that focus was moving up to item0,
        // which makes no sense
        item0.setExpanded(false);
        Toolkit.getToolkit().firePulse();
        assertEquals(3, treeView.getSelectionModel().getSelectedIndex());
        assertEquals(3, treeView.getFocusModel().getFocusedIndex());
    }

    @Test public void test_rt26721_collapseParent_firstRootChild() {
        treeView.setRoot(new TreeItem("Root"));
        treeView.getRoot().setExpanded(true);

        for (int i = 0; i < 4; i++) {
            TreeItem parent = new TreeItem("item - " + i);
            treeView.getRoot().getChildren().add(parent);

            for (int j = 0; j < 4; j++) {
                TreeItem child = new TreeItem("item - " + i + " " + j);
                parent.getChildren().add(child);
            }
        }

        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        final TreeItem<String> item0 = treeView.getTreeItem(1);
        final TreeItem<String> item0child0 = item0.getChildren().get(0);
        final TreeItem<String> item1 = treeView.getTreeItem(2);

        assertEquals("item - 0", item0.getValue());
        assertEquals("item - 1", item1.getValue());

        item0.setExpanded(true);
        item1.setExpanded(true);
        Toolkit.getToolkit().firePulse();

        // select the first child of item0
        treeView.getSelectionModel().select(item0child0);

        assertEquals(item0child0, treeView.getSelectionModel().getSelectedItem());
        assertEquals(item0child0, treeView.getFocusModel().getFocusedItem());

        // collapse item0 - we expect the selection / focus to move up to item0
        item0.setExpanded(false);
        Toolkit.getToolkit().firePulse();
        assertEquals(item0, treeView.getSelectionModel().getSelectedItem());
        assertEquals(item0, treeView.getFocusModel().getFocusedItem());
    }

    @Test public void test_rt26721_collapseParent_lastRootChild() {
        treeView.setRoot(new TreeItem("Root"));
        treeView.getRoot().setExpanded(true);

        for (int i = 0; i < 4; i++) {
            TreeItem parent = new TreeItem("item - " + i);
            treeView.getRoot().getChildren().add(parent);

            for (int j = 0; j < 4; j++) {
                TreeItem child = new TreeItem("item - " + i + " " + j);
                parent.getChildren().add(child);
            }
        }

        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        final TreeItem<String> item3 = treeView.getTreeItem(4);
        final TreeItem<String> item3child0 = item3.getChildren().get(0);

        assertEquals("item - 3", item3.getValue());
        assertEquals("item - 3 0", item3child0.getValue());

        item3.setExpanded(true);
        Toolkit.getToolkit().firePulse();

        // select the first child of item0
        treeView.getSelectionModel().select(item3child0);

        assertEquals(item3child0, treeView.getSelectionModel().getSelectedItem());
        assertEquals(item3child0, treeView.getFocusModel().getFocusedItem());

        // collapse item3 - we expect the selection / focus to move up to item3
        item3.setExpanded(false);
        Toolkit.getToolkit().firePulse();
        assertEquals(item3, treeView.getSelectionModel().getSelectedItem());
        assertEquals(item3, treeView.getFocusModel().getFocusedItem());
    }

    @Test public void test_rt26721_collapseGrandParent() {
        treeView.setRoot(new TreeItem("Root"));
        treeView.getRoot().setExpanded(true);

        for (int i = 0; i < 4; i++) {
            TreeItem parent = new TreeItem("item - " + i);
            treeView.getRoot().getChildren().add(parent);

            for (int j = 0; j < 4; j++) {
                TreeItem child = new TreeItem("item - " + i + " " + j);
                parent.getChildren().add(child);
            }
        }

        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        final TreeItem<String> item0 = treeView.getTreeItem(1);
        final TreeItem<String> item0child0 = item0.getChildren().get(0);
        final TreeItem<String> item1 = treeView.getTreeItem(2);

        assertEquals("item - 0", item0.getValue());
        assertEquals("item - 1", item1.getValue());

        item0.setExpanded(true);
        item1.setExpanded(true);
        Toolkit.getToolkit().firePulse();

        // select the first child of item0
        treeView.getSelectionModel().select(item0child0);

        assertEquals(item0child0, treeView.getSelectionModel().getSelectedItem());
        assertEquals(item0child0, treeView.getFocusModel().getFocusedItem());

        // collapse root - we expect the selection / focus to move up to root
        treeView.getRoot().setExpanded(false);
        Toolkit.getToolkit().firePulse();
        assertEquals(treeView.getRoot(), treeView.getSelectionModel().getSelectedItem());
        assertEquals(treeView.getRoot(), treeView.getFocusModel().getFocusedItem());
    }

    @Test public void test_rt34694() {
        TreeItem treeNode = new TreeItem("Controls");
        treeNode.getChildren().addAll(
            new TreeItem("Button"),
            new TreeItem("ButtonBar"),
            new TreeItem("LinkBar"),
            new TreeItem("LinkButton"),
            new TreeItem("PopUpButton"),
            new TreeItem("ToggleButtonBar")
        );

        final TreeView treeView = new TreeView();
        treeView.setRoot(treeNode);
        treeNode.setExpanded(true);

        treeView.getSelectionModel().select(0);
        assertTrue(treeView.getSelectionModel().isSelected(0));
        assertTrue(treeView.getFocusModel().isFocused(0));

        treeNode.getChildren().clear();
        treeNode.getChildren().addAll(
                new TreeItem("Button1"),
                new TreeItem("ButtonBar1"),
                new TreeItem("LinkBar1"),
                new TreeItem("LinkButton1"),
                new TreeItem("PopUpButton1"),
                new TreeItem("ToggleButtonBar1")
        );
        Toolkit.getToolkit().firePulse();

        assertTrue(treeView.getSelectionModel().isSelected(0));
        assertTrue(treeView.getFocusModel().isFocused(0));
    }

    private int test_rt_35213_eventCount = 0;
    @Test public void test_rt35213() {
        final TreeView<String> view = new TreeView<>();

        TreeItem<String> root = new TreeItem<>("Boss");
        view.setRoot(root);

        TreeItem<String> group1 = new TreeItem<>("Group 1");
        TreeItem<String> group2 = new TreeItem<>("Group 2");
        TreeItem<String> group3 = new TreeItem<>("Group 3");

        root.getChildren().addAll(group1, group2, group3);

        TreeItem<String> employee1 = new TreeItem<>("Employee 1");
        TreeItem<String> employee2 = new TreeItem<>("Employee 2");

        group2.getChildren().addAll(employee1, employee2);

        view.expandedItemCountProperty().addListener((observableValue, oldCount, newCount) -> {

            // DEBUG OUTPUT
//                System.out.println("new expanded item count: " + newCount.intValue());
//                for (int i = 0; i < newCount.intValue(); i++) {
//                    TreeItem<String> item = view.getTreeItem(i);
//                    String text = item.getValue();
//                    System.out.println("person found at index " + i + " is " + text);
//                }
//                System.out.println("------------------------------------------");

            if (test_rt_35213_eventCount == 0) {
                assertEquals(4, newCount);
                assertEquals("Boss", view.getTreeItem(0).getValue());
                assertEquals("Group 1", view.getTreeItem(1).getValue());
                assertEquals("Group 2", view.getTreeItem(2).getValue());
                assertEquals("Group 3", view.getTreeItem(3).getValue());
            } else if (test_rt_35213_eventCount == 1) {
                assertEquals(6, newCount);
                assertEquals("Boss", view.getTreeItem(0).getValue());
                assertEquals("Group 1", view.getTreeItem(1).getValue());
                assertEquals("Group 2", view.getTreeItem(2).getValue());
                assertEquals("Employee 1", view.getTreeItem(3).getValue());
                assertEquals("Employee 2", view.getTreeItem(4).getValue());
                assertEquals("Group 3", view.getTreeItem(5).getValue());
            } else if (test_rt_35213_eventCount == 2) {
                assertEquals(4, newCount);
                assertEquals("Boss", view.getTreeItem(0).getValue());
                assertEquals("Group 1", view.getTreeItem(1).getValue());
                assertEquals("Group 2", view.getTreeItem(2).getValue());
                assertEquals("Group 3", view.getTreeItem(3).getValue());
            }

            test_rt_35213_eventCount++;
        });

        StageLoader sl = new StageLoader(view);

        root.setExpanded(true);
        Toolkit.getToolkit().firePulse();

        group2.setExpanded(true);
        Toolkit.getToolkit().firePulse();

        group2.setExpanded(false);
        Toolkit.getToolkit().firePulse();

        sl.dispose();
    }

    @Test public void test_rt23245_itemIsInTree() {
        final TreeView<String> view = new TreeView<String>();
        final List<TreeItem<String>> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final TreeItem<String> item = new TreeItem<String>("Item" + i);
            item.setExpanded(true);
            items.add(item);
        }

        // link the items up so that the next item is the child of the current item
        for (int i = 0; i < 9; i++) {
            items.get(i).getChildren().add(items.get(i + 1));
        }

        view.setRoot(items.get(0));

        for (int i = 0; i < 10; i++) {
            // we expect the level of the tree item at the ith position to be
            // 0, as every iteration we are setting the ith item as the root.
            assertEquals(0, view.getTreeItemLevel(items.get(i)));

            // whilst we are testing, we should also ensure that the ith item
            // is indeed the root item, and that the ith item is indeed the item
            // at the 0th position
            assertEquals(items.get(i), view.getRoot());
            assertEquals(items.get(i), view.getTreeItem(0));

            // shuffle the next item into the root position (keeping its parent
            // chain intact - which is what exposes this issue in the first place).
            if (i < 9) {
                view.setRoot(items.get(i + 1));
            }
        }
    }

    @Test public void test_rt23245_itemIsNotInTree_noRootNode() {
        final TreeView<String> view = new TreeView<String>();
        final List<TreeItem<String>> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final TreeItem<String> item = new TreeItem<String>("Item" + i);
            item.setExpanded(true);
            items.add(item);
        }

        // link the items up so that the next item is the child of the current item
        for (int i = 0; i < 9; i++) {
            items.get(i).getChildren().add(items.get(i + 1));
        }

        for (int i = 0; i < 10; i++) {
            // because we have no root (and we are not changing the root like
            // the previous test), we expect the tree item level of the item
            // in the ith position to be i.
            assertEquals(i, view.getTreeItemLevel(items.get(i)));

            // all items requested from the TreeView should be null, as the
            // TreeView does not have a root item
            assertNull(view.getTreeItem(i));
        }
    }

    @Test public void test_rt23245_itemIsNotInTree_withUnrelatedRootNode() {
        final TreeView<String> view = new TreeView<String>();
        final List<TreeItem<String>> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final TreeItem<String> item = new TreeItem<String>("Item" + i);
            item.setExpanded(true);
            items.add(item);
        }

        // link the items up so that the next item is the child of the current item
        for (int i = 0; i < 9; i++) {
            items.get(i).getChildren().add(items.get(i + 1));
        }

        view.setRoot(new TreeItem("Unrelated root node"));

        for (int i = 0; i < 10; i++) {
            // because we have no root (and we are not changing the root like
            // the previous test), we expect the tree item level of the item
            // in the ith position to be i.
            assertEquals(i, view.getTreeItemLevel(items.get(i)));

            // all items requested from the TreeView should be null except for
            // the root node
            assertNull(view.getTreeItem(i + 1));
        }
    }

    @Test public void test_rt35039_setRoot() {
        TreeItem<String> root = new TreeItem<>("Root");
        root.setExpanded(true);
        root.getChildren().addAll(
                new TreeItem("aabbaa"),
                new TreeItem("bbc"));

        final TreeView<String> treeView = new TreeView<>();
        treeView.setRoot(root);

        StageLoader sl = new StageLoader(treeView);

        // We start with selection on row -1
        assertNull(treeView.getSelectionModel().getSelectedItem());

        // select "bbc" and ensure everything is set to that
        treeView.getSelectionModel().select(2);
        assertEquals("bbc", treeView.getSelectionModel().getSelectedItem().getValue());

        // change the items list - but retain the same content. We expect
        // that "bbc" remains selected as it is still in the list
        treeView.setRoot(root);
        assertEquals("bbc", treeView.getSelectionModel().getSelectedItem().getValue());

        sl.dispose();
    }

    @Test public void test_rt35039_resetRootChildren() {
        TreeItem aabbaa = new TreeItem("aabbaa");
        TreeItem bbc = new TreeItem("bbc");

        TreeItem<String> root = new TreeItem<>("Root");
        root.setExpanded(true);
        root.getChildren().setAll(aabbaa, bbc);

        final TreeView<String> treeView = new TreeView<>();
        treeView.setRoot(root);

        StageLoader sl = new StageLoader(treeView);

        // We start with selection on row -1
        assertNull(treeView.getSelectionModel().getSelectedItem());

        // select "bbc" and ensure everything is set to that
        treeView.getSelectionModel().select(2);
        assertEquals("bbc", treeView.getSelectionModel().getSelectedItem().getValue());

        // change the items list - but retain the same content. We expect
        // that "bbc" remains selected as it is still in the list
        root.getChildren().setAll(aabbaa, bbc);
        assertEquals("bbc", treeView.getSelectionModel().getSelectedItem().getValue());

        sl.dispose();
    }

    @Test public void test_rt35857() {
        TreeItem<String> root = new TreeItem<>("Root");
        root.setExpanded(true);
        TreeItem a = new TreeItem("A");
        TreeItem b = new TreeItem("B");
        TreeItem c = new TreeItem("C");
        root.getChildren().setAll(a, b, c);

        final TreeView<String> treeTableView = new TreeView<String>(root);

        treeTableView.getSelectionModel().select(1);

        ObservableList<TreeItem<String>> selectedItems = treeTableView.getSelectionModel().getSelectedItems();
        assertEquals(1, selectedItems.size());
        assertEquals("A", selectedItems.get(0).getValue());

        root.getChildren().removeAll(selectedItems);
        assertEquals(2, root.getChildren().size());
        assertEquals("B", root.getChildren().get(0).getValue());
        assertEquals("C", root.getChildren().get(1).getValue());
    }

    private int rt_35889_cancel_count = 0;
    @Test public void test_rt35889() {
        TreeItem a = new TreeItem("a");
        TreeItem b = new TreeItem("b");
        TreeItem<String> root = new TreeItem<>("Root");
        root.setExpanded(true);
        root.getChildren().setAll(a, b);

        final TreeView<String> textFieldTreeView = new TreeView<String>(root);
        textFieldTreeView.setEditable(true);
        textFieldTreeView.setCellFactory(TextFieldTreeCell.forTreeView());
        textFieldTreeView.setOnEditCancel(t -> {
            rt_35889_cancel_count++;
            System.out.println("On Edit Cancel: " + t);
        });

        TreeCell cell0 = (TreeCell) VirtualFlowTestUtils.getCell(textFieldTreeView, 0);
        assertNull(cell0.getGraphic());
        assertEquals("Root", cell0.getText());

        textFieldTreeView.edit(root);
        TextField textField = (TextField) cell0.getGraphic();
        assertNotNull(textField);

        assertEquals(0, rt_35889_cancel_count);

        textField.setText("Z");
        textField.getOnAction().handle(new ActionEvent());

        assertEquals(0, rt_35889_cancel_count);
    }

    @Test public void test_rt36255_selection_does_not_expand_item() {
        TreeItem a = new TreeItem("a");
        TreeItem b = new TreeItem("b");
        b.getChildren().add(new TreeItem("bb"));

        final TreeItem<String> root = new TreeItem<>();
        root.getChildren().addAll(a, b);
        root.setExpanded(true);
        TreeView<String> view = new TreeView<>(root);
        view.setCellFactory(TextFieldTreeCell.forTreeView());

        view.getSelectionModel().select(a);

        assertEquals(Arrays.asList(a), view.getSelectionModel().getSelectedItems());
        assertFalse(b.isExpanded());

        view.getSelectionModel().select(b);
        assertEquals(Arrays.asList(b), view.getSelectionModel().getSelectedItems());
        assertFalse(b.isExpanded());
    }

    @Test public void test_rt25679() {
        Button focusBtn = new Button("Focus here");

        TreeItem<String> root = new TreeItem<>("Root");
        root.getChildren().setAll(new TreeItem("a"), new TreeItem("b"));
        root.setExpanded(true);

        final TreeView<String> treeView = new TreeView<>(root);
        SelectionModel sm = treeView.getSelectionModel();

        VBox vbox = new VBox(focusBtn, treeView);

        StageLoader sl = new StageLoader(vbox);
        sl.getStage().requestFocus();
        focusBtn.requestFocus();
        Toolkit.getToolkit().firePulse();

        // test initial state
        assertEquals(sl.getStage().getScene().getFocusOwner(), focusBtn);
        assertTrue(focusBtn.isFocused());
        assertEquals(-1, sm.getSelectedIndex());
        assertNull(sm.getSelectedItem());

        // move focus to the treeview
        treeView.requestFocus();

        // ensure that there is a selection (where previously there was not one)
        assertEquals(sl.getStage().getScene().getFocusOwner(), treeView);
        assertTrue(treeView.isFocused());
        assertEquals(-1, sm.getSelectedIndex());
        assertNull(sm.getSelectedItem());

        sl.dispose();
    }

    @Test public void test_rt36885_addChildBeforeSelection() {
        test_rt36885(false);
    }

    @Test public void test_rt36885_addChildAfterSelection() {
        test_rt36885(true);
    }

    private void test_rt36885(boolean addChildToAAfterSelection) {
        TreeItem<String> root = new TreeItem<>("Root");     // 0
            TreeItem<String> a = new TreeItem<>("a");       // 1
                TreeItem<String> a1 = new TreeItem<>("a1"); // a expanded = 2, a collapsed = -1
            TreeItem<String> b = new TreeItem<>("b");       // a expanded = 3, a collapsed = 2
                TreeItem<String> b1 = new TreeItem<>("b1"); // a expanded = 4, a collapsed = 3
                TreeItem<String> b2 = new TreeItem<>("b2"); // a expanded = 5, a collapsed = 4

        root.setExpanded(true);
        root.getChildren().setAll(a, b);

        a.setExpanded(false);
        if (!addChildToAAfterSelection) {
            a.getChildren().add(a1);
        }

        b.setExpanded(true);
        b.getChildren().addAll(b1, b2);

        final TreeView<String> treeView = new TreeView<String>(root);

        treeView.getFocusModel().focusedIndexProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("focusedIndex: " + oldValue + " to " + newValue);
        });

        MultipleSelectionModel<TreeItem<String>> sm = treeView.getSelectionModel();
        FocusModel<TreeItem<String>> fm = treeView.getFocusModel();

        sm.select(b1);
        assertEquals(3, sm.getSelectedIndex());
        assertEquals(b1, sm.getSelectedItem());
        assertEquals(3, fm.getFocusedIndex());
        assertEquals(b1, fm.getFocusedItem());

        if (addChildToAAfterSelection) {
            a.getChildren().add(a1);
        }

        a.setExpanded(true);
        assertEquals(4, sm.getSelectedIndex());
        assertEquals(b1, sm.getSelectedItem());
        assertEquals(4, fm.getFocusedIndex());
        assertEquals(b1, fm.getFocusedItem());
    }

    private int rt_37061_index_counter = 0;
    private int rt_37061_item_counter = 0;
    @Test public void test_rt_37061() {
        TreeItem<Integer> root = new TreeItem<>(0);
        root.setExpanded(true);
        TreeView<Integer> tv = new TreeView<>();
        tv.setRoot(root);
        tv.getSelectionModel().select(0);

        // note we add the listeners after the selection is made, so the counters
        // at this point are still both at zero.
        tv.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            rt_37061_index_counter++;
        });

        tv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            rt_37061_item_counter++;
        });

        // add a new item. This does not impact the selected index or selected item
        // so the counters should remain at zero.
        tv.getRoot().getChildren().add(new TreeItem("1"));
        assertEquals(0, rt_37061_index_counter);
        assertEquals(0, rt_37061_item_counter);
    }

    private int rt_37395_index_addCount = 0;
    private int rt_37395_index_removeCount = 0;
    private int rt_37395_index_permutationCount = 0;
    private int rt_37395_item_addCount = 0;
    private int rt_37395_item_removeCount = 0;
    private int rt_37395_item_permutationCount = 0;

    @Test public void test_rt_37395() {
        // tree items - 3 items, 2nd item has 2 children
        TreeItem<String> root = new TreeItem<>();

        TreeItem<String> two = new TreeItem<>("two");
        two.getChildren().add(new TreeItem<>("childOne"));
        two.getChildren().add(new TreeItem<>("childTwo"));

        root.getChildren().add(new TreeItem<>("one"));
        root.getChildren().add(two);
        root.getChildren().add(new TreeItem<>("three"));

        // tree
        TreeView<String> tree = new TreeView<>();
        tree.setShowRoot(false);
        tree.setRoot(root);

        MultipleSelectionModel sm = tree.getSelectionModel();
        sm.getSelectedIndices().addListener(new ListChangeListener<Integer>() {
            @Override public void onChanged(Change<? extends Integer> c) {
                while (c.next()) {
                    if (c.wasRemoved()) {
                        c.getRemoved().forEach(item -> {
                            if (item == null) {
                                fail("Removed index should never be null");
                            } else {
                                rt_37395_index_removeCount++;
                            }
                        });
                    }
                    if (c.wasAdded()) {
                        c.getAddedSubList().forEach(item -> {
                            rt_37395_index_addCount++;
                        });
                    }
                    if (c.wasPermutated()) {
                        rt_37395_index_permutationCount++;
                    }
                }
            }
        });
        sm.getSelectedItems().addListener(new ListChangeListener<TreeItem<String>>() {
            @Override public void onChanged(Change<? extends TreeItem<String>> c) {
                while (c.next()) {
                    if (c.wasRemoved()) {
                        c.getRemoved().forEach(item -> {
                            if (item == null) {
                                fail("Removed item should never be null");
                            } else {
                                rt_37395_item_removeCount++;
                            }
                        });
                    }
                    if (c.wasAdded()) {
                        c.getAddedSubList().forEach(item -> {
                            rt_37395_item_addCount++;
                        });
                    }
                    if (c.wasPermutated()) {
                        rt_37395_item_permutationCount++;
                    }
                }
            }
        });

        assertEquals(0, rt_37395_index_removeCount);
        assertEquals(0, rt_37395_index_addCount);
        assertEquals(0, rt_37395_index_permutationCount);
        assertEquals(0, rt_37395_item_removeCount);
        assertEquals(0, rt_37395_item_addCount);
        assertEquals(0, rt_37395_item_permutationCount);

        StageLoader sl = new StageLoader(tree);

        // step one: select item 'three' in index 2
        sm.select(2);
        assertEquals(0, rt_37395_index_removeCount);
        assertEquals(1, rt_37395_index_addCount);
        assertEquals(0, rt_37395_index_permutationCount);
        assertEquals(0, rt_37395_item_removeCount);
        assertEquals(1, rt_37395_item_addCount);
        assertEquals(0, rt_37395_item_permutationCount);

        // step two: expand item 'two'
        // The first part of the bug report was that we received add/remove
        // change events here, when in reality we shouldn't have, so lets enforce
        // that. We do expect a permutation event on the index, as it has been
        // pushed down, but this should not result in an item permutation event,
        // as it remains unchanged
        two.setExpanded(true);
        assertEquals(0, rt_37395_index_removeCount);
        assertEquals(1, rt_37395_index_addCount);
        assertEquals(1, rt_37395_index_permutationCount);
        assertEquals(0, rt_37395_item_removeCount);
        assertEquals(1, rt_37395_item_addCount);
        assertEquals(0, rt_37395_item_permutationCount);

        // step three: collapse item 'two'
        // Same argument as in step two above: no addition or removal, just a
        // permutation on the index
        two.setExpanded(false);
        assertEquals(0, rt_37395_index_removeCount);
        assertEquals(1, rt_37395_index_addCount);
        assertEquals(2, rt_37395_index_permutationCount);
        assertEquals(0, rt_37395_item_removeCount);
        assertEquals(1, rt_37395_item_addCount);
        assertEquals(0, rt_37395_item_permutationCount);

        sl.dispose();
    }

    @Test public void test_rt_37502() {
        final TreeView<Long> tree = new TreeView<>(new NumberTreeItem(1));
        tree.setCellFactory(new Callback<TreeView<Long>, TreeCell<Long>>() {
            @Override
            public TreeCell<Long> call(TreeView<Long> param) {
                return new TreeCell<Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            setText(item != null ? String.valueOf(item) : "");
                        } else{
                            setText(null);
                        }
                    }
                };
            }
        });

        StageLoader sl = new StageLoader(tree);

        tree.getSelectionModel().select(0);
        tree.getRoot().setExpanded(true);
        Toolkit.getToolkit().firePulse();

        sl.dispose();
    }

    private static class NumberTreeItem extends TreeItem<Long>{
        private boolean loaded = false;

        private NumberTreeItem(long value) {
            super(value);
        }

        @Override public boolean isLeaf() {
            return false;
        }

        @Override public ObservableList<TreeItem<Long>> getChildren() {
            if(!loaded){
                final ObservableList<TreeItem<Long>> children =  super.getChildren();
                for (int i = 0; i < 10; i++) {
                    children.add(new NumberTreeItem(10 * getValue() + i));
                }
                loaded = true;
            }
            return super.getChildren();
        }
    }

    private int rt_37538_count = 0;
    @Test public void test_rt_37538_noCNextCall() {
        test_rt_37538(false, false);
    }

    @Test public void test_rt_37538_callCNextOnce() {
        test_rt_37538(true, false);
    }

    @Test public void test_rt_37538_callCNextInLoop() {
        test_rt_37538(false, true);
    }

    private void test_rt_37538(boolean callCNextOnce, boolean callCNextInLoop) {
        // create table with a bunch of rows and 1 column...
        TreeItem<Integer> root = new TreeItem<>(0);
        root.setExpanded(true);
        for (int i = 1; i <= 50; i++) {
            root.getChildren().add(new TreeItem<>(i));
        }

        final TreeView<Integer> tree = new TreeView<>(root);

        tree.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends TreeItem<Integer>> c) -> {
            if (callCNextOnce) {
                c.next();
            } else if (callCNextInLoop) {
                while (c.next()) {
                    // no-op
                }
            }

            if (rt_37538_count >= 1) {
                Thread.dumpStack();
                fail("This method should only be called once");
            }

            rt_37538_count++;
        });

        StageLoader sl = new StageLoader(tree);
        assertEquals(0, rt_37538_count);
        tree.getSelectionModel().select(0);
        assertEquals(1, rt_37538_count);
        sl.dispose();
    }

    @Ignore("Fix not yet developed for TreeView")
    @Test public void test_rt_35395_fixedCellSize() {
        test_rt_35395(true);
    }

    @Ignore("Fix not yet developed for TreeView")
    @Test public void test_rt_35395_notFixedCellSize() {
        test_rt_35395(false);
    }

    private int rt_35395_counter;
    private void test_rt_35395(boolean useFixedCellSize) {
        rt_35395_counter = 0;

        TreeItem<String> root = new TreeItem<>("green");
        root.setExpanded(true);
        for (int i = 0; i < 20; i++) {
            root.getChildren().addAll(new TreeItem<>("red"), new TreeItem<>("green"), new TreeItem<>("blue"), new TreeItem<>("purple"));
        }

        TreeView<String> treeView = new TreeView<>(root);
        if (useFixedCellSize) {
            treeView.setFixedCellSize(24);
        }
        treeView.setCellFactory(tv -> new TreeCell<String>() {
            @Override protected void updateItem(String color, boolean empty) {
                rt_35395_counter += 1;
                super.updateItem(color, empty);
                setText(null);
                if(empty) {
                    setGraphic(null);
                } else {
                    Rectangle rect = new Rectangle(16, 16);
                    rect.setStyle("-fx-fill: " + color);
                    setGraphic(rect);
                }
            }
        });

        StageLoader sl = new StageLoader(treeView);

        Platform.runLater(() -> {
            rt_35395_counter = 0;
            root.getChildren().set(10, new TreeItem<>("yellow"));
            Platform.runLater(() -> {
                Toolkit.getToolkit().firePulse();
                assertEquals(1, rt_35395_counter);
                rt_35395_counter = 0;
                root.getChildren().set(30, new TreeItem<>("yellow"));
                Platform.runLater(() -> {
                    Toolkit.getToolkit().firePulse();
                    assertEquals(0, rt_35395_counter);
                    rt_35395_counter = 0;
                    treeView.scrollTo(5);
                    Platform.runLater(() -> {
                        Toolkit.getToolkit().firePulse();
                        assertEquals(5, rt_35395_counter);
                        rt_35395_counter = 0;
                        treeView.scrollTo(55);
                        Platform.runLater(() -> {
                            Toolkit.getToolkit().firePulse();

                            int expected = useFixedCellSize ? 17 : 53;
                            assertEquals(expected, rt_35395_counter);
                            sl.dispose();
                        });
                    });
                });
            });
        });
    }

    @Test public void test_rt_37632() {
        final TreeItem<String> rootOne = new TreeItem<>("Root 1");
        final TreeItem<String> rootTwo = new TreeItem<>("Root 2");

        final TreeView<String> treeView = new TreeView<>();
        MultipleSelectionModel<TreeItem<String>> sm = treeView.getSelectionModel();
        treeView.setRoot(rootOne);
        treeView.getSelectionModel().selectFirst();

        assertEquals(0, sm.getSelectedIndex());
        assertEquals(rootOne, sm.getSelectedItem());
        assertEquals(1, sm.getSelectedIndices().size());
        assertEquals(0, (int) sm.getSelectedIndices().get(0));
        assertEquals(1, sm.getSelectedItems().size());
        assertEquals(rootOne, sm.getSelectedItems().get(0));

        treeView.setRoot(rootTwo);

        assertEquals(-1, sm.getSelectedIndex());
        assertNull(sm.getSelectedItem());
        assertEquals(0, sm.getSelectedIndices().size());
        assertEquals(0, sm.getSelectedItems().size());
    }

    @Test public void test_rt_37853_replaceRoot() {
        test_rt_37853(true);
    }

    @Test public void test_rt_37853_replaceRootChildren() {
        test_rt_37853(false);
    }

    private int rt_37853_cancelCount;
    private int rt_37853_commitCount;
    private void test_rt_37853(boolean replaceRoot) {
        treeView.setCellFactory(TextFieldTreeCell.forTreeView());
        treeView.setEditable(true);
        treeView.setRoot(new TreeItem<>("Root"));
        treeView.getRoot().setExpanded(true);

        for (int i = 0; i < 10; i++) {
            treeView.getRoot().getChildren().add(new TreeItem<>("" + i));
        }

        StageLoader sl = new StageLoader(treeView);

        treeView.setOnEditCancel(editEvent -> rt_37853_cancelCount++);
        treeView.setOnEditCommit(editEvent -> rt_37853_commitCount++);

        assertEquals(0, rt_37853_cancelCount);
        assertEquals(0, rt_37853_commitCount);

        treeView.edit(treeView.getRoot().getChildren().get(0));
        assertNotNull(treeView.getEditingItem());

        if (replaceRoot) {
            treeView.setRoot(new TreeItem<>("New Root"));
        } else {
            treeView.getRoot().getChildren().clear();
            for (int i = 0; i < 10; i++) {
                treeView.getRoot().getChildren().add(new TreeItem<>("new item " + i));
            }
        }

        assertEquals(1, rt_37853_cancelCount);
        assertEquals(0, rt_37853_commitCount);

        sl.dispose();
    }

    @Test public void test_rt_38787_remove_b() {
        // Remove 'b', selection moves to 'a'
        test_rt_38787("a", 0, 1);
    }

    @Test public void test_rt_38787_remove_b_c() {
        // Remove 'b' and 'c', selection moves to 'a'
        test_rt_38787("a", 0, 1, 2);
    }

    @Test public void test_rt_38787_remove_c_d() {
        // Remove 'c' and 'd', selection moves to 'b'
        test_rt_38787("b", 1, 2, 3);
    }

    @Test public void test_rt_38787_remove_a() {
        // Remove 'a', selection moves to 'b', now in index 0
        test_rt_38787("b", 0, 0);
    }

    private void test_rt_38787(String expectedItem, int expectedIndex, int... indicesToRemove) {
        TreeItem<String> a, b, c, d;
        TreeItem<String> root = new TreeItem<>("Root");
        root.setExpanded(true);
        root.getChildren().addAll(
                a = new TreeItem<String>("a"),
                b = new TreeItem<String>("b"),
                c = new TreeItem<String>("c"),
                d = new TreeItem<String>("d")
        );

        TreeView<String> stringTreeView = new TreeView<>(root);
        stringTreeView.setShowRoot(false);

//        TableColumn<String,String> column = new TableColumn<>("Column");
//        column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue()));
//        stringTableView.getColumns().add(column);

        MultipleSelectionModel<TreeItem<String>> sm = stringTreeView.getSelectionModel();
        sm.select(b);

        // test pre-conditions
        assertEquals(1, sm.getSelectedIndex());
        assertEquals(1, (int)sm.getSelectedIndices().get(0));
        assertEquals(b, sm.getSelectedItem());
        assertEquals(b, sm.getSelectedItems().get(0));
        assertFalse(sm.isSelected(0));
        assertTrue(sm.isSelected(1));
        assertFalse(sm.isSelected(2));

        // removing items
        List<TreeItem<String>> itemsToRemove = new ArrayList<>(indicesToRemove.length);
        for (int index : indicesToRemove) {
            itemsToRemove.add(root.getChildren().get(index));
        }
        root.getChildren().removeAll(itemsToRemove);

        // testing against expectations
        assertEquals(expectedIndex, sm.getSelectedIndex());
        assertEquals(expectedIndex, (int)sm.getSelectedIndices().get(0));
        assertEquals(expectedItem, sm.getSelectedItem().getValue());
        assertEquals(expectedItem, sm.getSelectedItems().get(0).getValue());
    }

    private int rt_38341_indices_count = 0;
    private int rt_38341_items_count = 0;
    @Test public void test_rt_38341() {
        Callback<Integer, TreeItem<String>> callback = number -> {
            final TreeItem<String> root = new TreeItem<>("Root " + number);
            final TreeItem<String> child = new TreeItem<>("Child " + number);

            root.getChildren().add(child);
            return root;
        };

        final TreeItem<String> root = new TreeItem<String>();
        root.setExpanded(true);
        root.getChildren().addAll(callback.call(1), callback.call(2));

        final TreeView<String> treeView = new TreeView<>(root);
        treeView.setShowRoot(false);

        MultipleSelectionModel<TreeItem<String>> sm = treeView.getSelectionModel();
        sm.getSelectedIndices().addListener((ListChangeListener<Integer>) c -> rt_38341_indices_count++);
        sm.getSelectedItems().addListener((ListChangeListener<TreeItem<String>>) c -> rt_38341_items_count++);

        assertEquals(0, rt_38341_indices_count);
        assertEquals(0, rt_38341_items_count);

        // expand the first child of root, and select it (note: root isn't visible)
        root.getChildren().get(0).setExpanded(true);
        sm.select(1);
        assertEquals(1, sm.getSelectedIndex());
        assertEquals(1, sm.getSelectedIndices().size());
        assertEquals(1, (int)sm.getSelectedIndices().get(0));
        assertEquals(1, sm.getSelectedItems().size());
        assertEquals("Child 1", sm.getSelectedItem().getValue());
        assertEquals("Child 1", sm.getSelectedItems().get(0).getValue());

        assertEquals(1, rt_38341_indices_count);
        assertEquals(1, rt_38341_items_count);

        // now delete it
        root.getChildren().get(0).getChildren().remove(0);

        // selection should move to the childs parent in index 0
        assertEquals(0, sm.getSelectedIndex());
        assertEquals(1, sm.getSelectedIndices().size());
        assertEquals(0, (int)sm.getSelectedIndices().get(0));
        assertEquals(1, sm.getSelectedItems().size());
        assertEquals("Root 1", sm.getSelectedItem().getValue());
        assertEquals("Root 1", sm.getSelectedItems().get(0).getValue());

        // we also expect there to be an event in the selection model for
        // selected indices and selected items
        assertEquals(2, rt_38341_indices_count);
        assertEquals(2, rt_38341_items_count);
    }

    private int rt_38943_index_count = 0;
    private int rt_38943_item_count = 0;
    @Test public void test_rt_38943() {
        TreeItem<String> root = new TreeItem<>("Root");
        root.setExpanded(true);
        root.getChildren().addAll(
            new TreeItem<>("a"),
            new TreeItem<>("b"),
            new TreeItem<>("c"),
            new TreeItem<>("d")
        );

        final TreeView<String> treeView = new TreeView<>(root);
        treeView.setShowRoot(false);

        MultipleSelectionModel<TreeItem<String>> sm = treeView.getSelectionModel();

        sm.selectedIndexProperty().addListener((observable, oldValue, newValue) -> rt_38943_index_count++);
        sm.selectedItemProperty().addListener((observable, oldValue, newValue) -> rt_38943_item_count++);

        assertEquals(-1, sm.getSelectedIndex());
        assertNull(sm.getSelectedItem());
        assertEquals(0, rt_38943_index_count);
        assertEquals(0, rt_38943_item_count);

        sm.select(0);
        assertEquals(0, sm.getSelectedIndex());
        assertEquals("a", sm.getSelectedItem().getValue());
        assertEquals(1, rt_38943_index_count);
        assertEquals(1, rt_38943_item_count);

        sm.clearSelection(0);
        assertEquals(-1, sm.getSelectedIndex());
        assertNull(sm.getSelectedItem());
        assertEquals(2, rt_38943_index_count);
        assertEquals(2, rt_38943_item_count);
    }
}
