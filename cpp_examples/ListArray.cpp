// @authors David Fletcher Anna Cummings
// @date November 14, 2016

#ifndef LISTARRAY_CPP
#define LISTARRAY_CPP

#include <iostream>
#include "ListArray.h"

using namespace std;

//template <class DataType>
template <typename DataType>
List<DataType>::List()
// Initialize the object's member variables
: maxSize(defaultMaxSize), size(0), cursor(-1)
{
    // Make the array to put the items in and point dataItems there.
    this->dataItems = new DataType[maxSize];
}

template <typename DataType>
List<DataType>::List( int maxItems )
// You do: fill in code for this function.
{
	this->dataItems = new DataType[maxItems];
	this->maxSize = maxItems;
	this->size = 0;
	this->cursor = -1;
}

template <typename DataType>
List<DataType>::List( const List<DataType>& other ) {
    // You do: create dataItems array and copy all valid data from the other list
	this->maxSize = other.maxSize;
	this->size = other.size;
	this->cursor = other.cursor;
	this->dataItems = new DataType[maxSize];
	for(int i = 0; i < size; i++) {
		this->dataItems[i] = other.dataItems[i];
	}
}

template <typename DataType>
List<DataType>::~List() {
    // You do: return dynamically allocated memory
	delete[] this->dataItems;
}

template <typename DataType>
List<DataType>& List<DataType>::operator=( const List<DataType>& other ) {
    // Check to see that you are not being asked to assign yourself to yourself
    // Do that by comparing address of other list to your address.
    if( &other == this ) return *this;

    // You do: delete your dataItems array
	delete[] this->dataItems;

    // You do: make a new dataItems array of the correct size
	this->maxSize = other.maxSize;
	this->size = other.size;
	this->cursor = other.cursor;
	this->dataItems = new DataType[maxSize];
	for(int i = 0; i < size; i++) {
		this->dataItems[i] = other.dataItems[i];
	}

    // To get chained assignment working, return a reference to yourself
    // by doing following. Chained assignment is "a = b = c".
    return *this;
}

// Iterators

template <typename DataType>
bool List<DataType>::gotoNext() {
    // Cannot advance cursor if the list is empty OR
    // the cursor is not currently on the last valid data item.
    // Return false to indicate failure to advance the cursor
    if( isEmpty() || cursor == size-1 ) {
        return false;
    }
    
    // Otherwise all is well. Advance cursor and return success.
    else {
        cursor++;
	return true;
    }
}

template <typename DataType>
bool List<DataType>::gotoPrior() {
    if( isEmpty() || cursor == 0 ) { return false; }
    else {
    	cursor--;
	return true;
    }
}

template <typename DataType>
void List<DataType>::gotoBeginning() {
    // If the list is empty, nothing to do. So just return.
    if( isEmpty() ) {
        return;
    }

    // Otherwise, move cursor to mark first item.
    else {
        cursor = 0;
    }
}

template <typename DataType>
void List<DataType>::gotoEnd() {
    cursor = size - 1;
}

// Observers

template <typename DataType>
bool List<DataType>::isEmpty() const {
   return (size == 0);
}

template <typename DataType>
bool List<DataType>::isFull() const {
    if( size == maxSize ) {
        return true;
    } else {
        return false;
    }

    // Or more cleanly, do all of the above in one line.
    //    "return size == maxSize;"
}

template <typename DataType>
int List<DataType>::getCursorPosition() const {
    return cursor;
}

template <typename DataType>
int List<DataType>::getSize() const {
    return size;
}

template <typename DataType>
DataType List<DataType>::getCursor() const throw (logic_error) {
	if( isEmpty() ) { throw logic_error("List is empty!"); }

	return dataItems[cursor];
}


template <typename DataType>
DataType List<DataType>::operator[]( int position ) const throw (logic_error) {
    // If an invalid position is requested, throw an exception back at the caller.

    if( position<0 || position>=size ) { throw logic_error("invalid list position requested"); }
    
    // You do: else return the item requested
    return dataItems[position];
}

template <typename DataType>
void List<DataType>::showStructure() const 		// Debugging function
// Outputs the data items in a list. if the list is empty, outputs
// "empty list". this operation is intended for testing/debugging
// purposes only.

{
    int j;   // loop counter

    if ( size == 0 )
       cout << "empty list" << endl;
    else
    {
       cout << "size = " << size
            <<  "   cursor = " << cursor << endl;
       for ( j = 0 ; j < maxSize ; j++ )
           cout << j << "\t";
       cout << endl;
       for ( j = 0 ; j < size ; j++ ) {
	   if( j == cursor ) {
	      cout << "[";
              cout << dataItems[j];
	      cout << "]";
 	      cout << "\t";
	   }
	   else
	      cout << dataItems[j] << "\t";
       }
       cout << endl;
    }
}

// Transformers/mutators
template <typename DataType>
bool List<DataType>::search( DataType value ) {
    for ( int i=0; i<size; i++ ) {
    	if (dataItems[i] == value) { return true; }
    }

    return false;
}

template <typename DataType>
void List<DataType>::add( const DataType& newItem ) throw (logic_error) {
	if( size == maxSize ) { throw logic_error( "List is full!" ); }

	else {
		cursor++;
		for( int index = size; index > cursor; index-- ) {
			dataItems[index] = dataItems[index-1];
		}
		dataItems[cursor] = newItem;
		size++;
	}
}

template <typename DataType>
void List<DataType>::add( const List<DataType>& otherList ) throw (logic_error) {
	if( size == maxSize || size + otherList.size > maxSize) { throw logic_error( "List is full!" ); }

	else {
		cursor += otherList.size;
		for( int index = size + otherList.size - 1; index > cursor; index-- ) {
			dataItems[index] = dataItems[index-otherList.size];
		}
		for ( int index = otherList.size - 1; index >= 0; index-- ) {
			dataItems[cursor] = otherList[index];
			cursor--;
		}
		size = otherList.size + dataItems.size;
		cursor = size -1;
	}
    
}

template <typename DataType>
void List<DataType>::remove() throw (logic_error) {
    if ( isEmpty() ) { throw logic_error ("List is empty!"); }

    for( int i = cursor; i < size-1; i++ ) {
	dataItems[i] = dataItems[i+1];
    }
	size--;

	if( cursor == size ) {
		cursor = 0;
	}
}

template <typename DataType>
void List<DataType>::remove( const DataType& delItem ) {
    for ( int i=0; i<size; i++ ) {
    	if (dataItems[i] == delItem) {
		cursor = i;
	}
    }
    for( int i = cursor; i < size-1; i++ ) {
	dataItems[i] = dataItems[i+1];
    }
	size--;	    
}

template <typename DataType>
void List<DataType>::replace( const DataType& replaceItem ) throw (logic_error) {
    if ( isEmpty() ) { throw logic_error ("List is empty!"); }
    dataItems[cursor] = replaceItem;
}

template <typename DataType>
void List<DataType>::clear() {
    // Don't throw away the array. Just declare it to be empty.
    size = 0;
    cursor = -1;
}

#endif     // #ifndef LIST_CPP
