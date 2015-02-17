// var raw =  copy contents of item-array-formatter-test-data1.js
var func1 = function() {
  var items = [];
  for (i in raw) {
    var item3Elem = raw[i];

    // move the first elem into the actual array, and remove the last elem
    var item = firstPass(item3Elem);
    
    // join flavor text
    // note that Nashorn 8 does not support join
    item['flavourText'] = join(item['flavourText'], '\n');
    
    // remove complex data for now
    delete item.properties
    delete item.requirements
    delete item.socketedItems
    delete item.sockets
    
	  items.push(item);
    
  }
  return items;
}


function firstPass(itemRaw) {
  var indexInShop = itemRaw[0];
  itemRaw[1]['indexInShop'] = indexInShop;
  return itemRaw[1];
}

function join(arr, delim) {
  var s = "";
  for(i in arr) {
    s = s + arr[i] + delim;
  }
  s = s.substring(0, s.length - delim.length);
  return s;
}