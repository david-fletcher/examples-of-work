#ifndef LISTLINKED_H
#define LISTLINKED_H

#include <stdexcept>

using namespace std;

template <typename DataType>
class List {
  public:
    // Constructors, destructor, and related
    List();
    List(int /* maxItems */);
    List(const List& other);
    virtual ~List();
    List& operator=(const List& other);

    // Iterators
    bool gotoNext();
    bool gotoPrior();
    void gotoBeginning();
    void gotoEnd();

    // Observers
    bool isEmpty() const;
    bool isFull() const;
    int getCursorPosition() const;
    int getSize() const;
    DataType getCursor() const throw (logic_error);
    DataType operator[]( int position ) const throw (logic_error);
    void showStructure() const;		// Debugging function

    // Transformers/mutators
    bool search(DataType value);
    void add( const DataType& newItem ) throw (logic_error);
    void add( const List& otherList ) throw (logic_error);
    void remove() throw (logic_error);
    void remove( const DataType& delItem );
    void replace( const DataType& replaceItem ) throw (logic_error);
    void clear();

  protected:
    // Data members
    class ListNode {
      public:
	ListNode( DataType newData, ListNode* next );
	DataType dataItem;
	ListNode* next;
    };
    ListNode *head;	// Pointer to first node in list
    ListNode *cursor;	// Pointer to current node  in list
};

#endif
