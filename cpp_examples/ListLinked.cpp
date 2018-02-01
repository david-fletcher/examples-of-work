#ifndef LISTLINKED_CPP
#define LISTLINKED_CPP

#include <iostream>
#include <stdexcept>

#include "ListLinked.h"

using namespace std;

template <typename DataType>
List<DataType>::ListNode::ListNode( DataType newData, ListNode* next ) {
	this->dataItem = newData;
	this->next = next;
}

template <typename DataType>
List<DataType>::List() {
	this->head = 0;
	this->cursor = 0;
}

template <typename DataType>
List<DataType>::List(int /* maxItems */) {
	this->head = 0;
	this->cursor = 0;
}

template <typename DataType>
List<DataType>::List(const List& other) {
	this->head = 0;
	this->cursor = 0;
	add( other );
}

template <typename DataType>
List<DataType>::~List() {
	clear();
}

template <typename DataType>
List<DataType>& List<DataType>::operator=(const List& other) {
	if( this != &other) {
		clear();
		add( other );
	}
	return *this;
}


// Iterators
template <typename DataType>
bool List<DataType>::gotoNext() {
	if( isEmpty() ) { return false; }
	else if( cursor->next == 0 ) { return false; }
	else {
		cursor = cursor->next;
		return true;
	}
}

template <typename DataType>
bool List<DataType>::gotoPrior() {
	if (cursor == head) {return false;}
	else {
		ListNode* findMe = cursor;
		cursor = head;
		while ( cursor->next != findMe ) {
			cursor = cursor->next;
		}
	}

}

template <typename DataType>
void List<DataType>::gotoBeginning() {
	cursor = head;
}

template <typename DataType>
void List<DataType>::gotoEnd() {
	cursor = head;
	while( cursor->next != 0 ) {
		cursor = cursor->next;
	}
}


// Observers
template <typename DataType>
bool List<DataType>::isEmpty() const {
	return head == 0;
}

template <typename DataType>
bool List<DataType>::isFull() const {
	return false;
}

template <typename DataType>
int List<DataType>::getCursorPosition() const {
	int count = 0;
	ListNode* countMe = head;
	if( isEmpty() ) { return -1; }
	else {
		while( countMe != cursor ) {
			countMe = countMe->next;
			count++;
		}
		return count;
	}
}

template <typename DataType>
int List<DataType>::getSize() const {
	int count = 0;
	ListNode* countMe = head;
	while( countMe != 0 ) {
		countMe = countMe->next;
		count++;
	}
	return count;
}

template <typename DataType>
DataType List<DataType>::getCursor() const throw (logic_error) {
	if( cursor == 0 ) { throw logic_error("List is empty!"); }
	else { 
		return cursor->dataItem;
	}
}

template <typename DataType>
DataType List<DataType>::operator[]( int position ) const throw (logic_error) {
	int count = 0;
	while( count != position ) {
		if( cursor == 0 ) { throw logic_error("position is out of bounds!"); }
		else {
			cursor = cursor->next;
			count++;
		}
	}
	return cursor->dataItem;
}

template <typename DataType>
void List<DataType>::showStructure() const {

// Outputs the items in a list. If the list is empty, outputs
// "Empty list". This operation is intended for testing and
// debugging purposes only.

    if ( isEmpty() )
    {
       cout << "Empty list" << endl;
    } 
    else
    {
	for (ListNode* temp = head; temp != 0; temp = temp->next) {
	    if (temp == cursor) {
		cout << "[";
	    }

	    // Assumes that dataItem can be printed via << because
	    // is is either primitive or operator<< is overloaded.
	    cout << temp->dataItem;	

	    if (temp == cursor) {
		cout << "]";
	    }
	    cout << " ";
	}
	cout << endl;
    }
}
		// Debugging function

// Transformers/mutators
template <typename DataType>
bool List<DataType>::search(DataType value) {
	ListNode* findMe = head;
	while( findMe != 0 ) {
		if( cursor->dataItem == value ) {
			return true;
		}
		findMe = findMe->next;
	}
	return false;
}

template <typename DataType>
void List<DataType>::add( const DataType& newItem ) throw (logic_error) {
	if( isEmpty() ) { // beginning of the list
		ListNode* newNode = new ListNode( newItem, 0 );
		head = newNode;
		cursor = newNode;
		//head = cursor = new ListNode( newItem, 0 ); // chain assignment; both head and cursor need to be equal to the new Node
	}
	else { // end of the list	
		ListNode* newNode = new ListNode( newItem, cursor->next );
		cursor->next = newNode;  // (1): create a new Node, and point the Node at cursor towards new Node
		cursor = cursor->next;   // (2): point the cursor at the newest Node
	}
}

template <typename DataType>
void List<DataType>::add( const List& otherList ) throw (logic_error) {
	ListNode* addNode = otherList.head;
	while( addNode != 0 ) {
		add( addNode->dataItem );
		addNode = addNode->next;
	}
}

template <typename DataType>
void List<DataType>::remove() throw (logic_error) {
	if ( isEmpty() ) throw logic_error("The list is empty!!!!");
	else if ( head == cursor ) {
		cursor = cursor->next;
		delete head;
		head = cursor;
	}
	else {
		ListNode* zapMe = cursor;
		gotoPrior();
		cursor->next = zapMe->next;
		delete zapMe;
		cursor = cursor->next;
		if( cursor == 0 ) {
			cursor = head;
		}
	}
}

template <typename DataType>
void List<DataType>::remove( const DataType& delItem ) {
	ListNode* zapMe = head;
	while( zapMe != 0 ) {
		if( zapMe->dataItem == delItem ) {
			remove(zapMe);
			return;
		}
		zapMe = zapMe->next;
	}
	return;
}

template <typename DataType>
void List<DataType>::replace( const DataType& replaceItem ) throw (logic_error) {
	if( head == 0 ) { throw logic_error("List is empty!"); }
	else {
		cursor->dataItem = replaceItem;
	}
}

template <typename DataType>
void List<DataType>::clear() {
	cursor = head;
	while ( cursor != 0 ) {
		cursor = cursor->next;
		delete head;
		head = cursor;
	}
}

#endif
