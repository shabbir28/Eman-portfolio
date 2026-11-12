"use strict";
const definitions = [
  ["Furniture", "Furniture", ["Bed", "Desk", "Chair"]],
  ["Electronics", "Electronics", ["Television", "Lamp", "Electric kettle"]],
  ["Miscellaneous", "Miscellaneous", ["Wall clock", "Waste basket"]],
  ["Cleaning", "Clean", ["Cleaning kit", "Mop"]],
  ["Kitchen", "Kitchen", ["Dinner plate", "Tea cup", "Serving tray"]],
  ["Laundry", "Laundry", ["Bath towel", "Bed sheet"]]
];
const rooms = ["Demo Room 101", "Demo Room 102", "Demo Reception"];
let items, records, category = "Furniture";
const $ = id => document.getElementById(id);
function seed() {
  items = Object.fromEntries(definitions.map(([name,,types]) => [name, [...types]]));
  records = definitions.flatMap(([name,,types]) => types.map((item,i) => ({category:name,item,room:name === "Kitchen" ? "Kitchen store" : rooms[i % rooms.length],quantity:name === "Kitchen" ? 12 + i * 6 : 2 + i})));
}
function message(text, error=false) { $("status").textContent=text; $("status").dataset.error=String(error); }
function options(select, values, all=false) {
  select.replaceChildren();
  if(all) select.add(new Option("All locations", ""));
  values.forEach(value => select.add(new Option(value,value)));
}
function render() {
  const roomView=category === "Room inventory";
  document.querySelectorAll("nav button").forEach(button => button.setAttribute("aria-pressed",String(button.dataset.category === category)));
  $("workspace-title").textContent=category;
  $("item-form").hidden=roomView; $("stock-form").hidden=roomView;
  // A hidden form must not retain its flex display from the panel styles.
  $("item-form").style.display=roomView ? "none" : "";
  $("stock-form").style.display=roomView ? "none" : "";
  if(!roomView) options($("stock-item"),items[category]);
  options($("stock-room"),category === "Kitchen" ? ["Kitchen store"] : rooms);
  options($("filter-room"),category === "Kitchen" ? ["Kitchen store"] : roomView ? [...rooms,"Kitchen store"] : rooms,true);
  $("search").value=""; $("new-item").value="";
  renderRows();
}
function renderRows() {
  const query=$("search").value.trim().toLowerCase(), location=$("filter-room").value;
  const scope=records.filter(r => category === "Room inventory" || r.category === category);
  const filtered=scope.filter(r=>(!location || r.room===location) && `${r.item} ${r.room} ${r.category}`.toLowerCase().includes(query));
  $("total").textContent=`${scope.reduce((sum,r)=>sum+r.quantity,0)} units · ${scope.length} records`;
  $("results-caption").textContent=`${category} · ${filtered.length} matching records`;
  $("rows").replaceChildren();
  for(const row of filtered) {
    const tr=document.createElement("tr");
    [row.category,row.item,row.room,row.quantity].forEach(value=>{const td=document.createElement("td");td.textContent=value;tr.append(td);});
    $("rows").append(tr);
  }
  if(!filtered.length){const tr=document.createElement("tr"),td=document.createElement("td");td.colSpan=4;td.className="empty";td.textContent="No matching inventory. Try another search or add sample stock.";tr.append(td);$("rows").append(tr);}
}
for(const [name,icon] of [...definitions,["Room inventory","room"]]) {
  const button=document.createElement("button");button.type="button";button.dataset.category=name;
  const image=document.createElement("img");image.alt="";image.src=`../../assets/hotel-demo/${icon === "room" ? "room.png" : icon+"_image-removebg-preview.png"}`;
  const label=document.createElement("span");label.textContent=name;button.append(image,label);
  button.addEventListener("click",()=>{category=name;render();message(name === "Room inventory" ? "Browse inventory across all categories by room." : `Manage ${name.toLowerCase()} using the sample inventory.`);});
  $("categories").append(button);
}
$("item-form").addEventListener("submit",e=>{
  e.preventDefault(); const name=$("new-item").value.trim().replace(/\s+/g," ");
  if(!name || name.length>60)return message("Enter an item name between 1 and 60 characters.",true);
  if(items[category].some(item=>item.toLowerCase()===name.toLowerCase()))return message("That item type already exists in this category.",true);
  items[category].push(name);options($("stock-item"),items[category]);$("stock-item").value=name;$("new-item").value="";message(`Added ${name}. Choose a location and quantity to add its stock.`);
});
$("stock-form").addEventListener("submit",e=>{
  e.preventDefault();const quantity=Number($("quantity").value),item=$("stock-item").value,room=$("stock-room").value;
  if(!Number.isSafeInteger(quantity)||quantity<1||quantity>9999)return message("Enter a whole quantity from 1 to 9,999.",true);
  if(!items[category]?.includes(item))return message("Choose an item first.",true);
  const record=records.find(r=>r.category===category&&r.item===item&&r.room===room);
  const remove=e.submitter?.value==="remove";
  if(remove && (!record || record.quantity<quantity))return message(`Cannot remove ${quantity}. Available in ${room}: ${record?.quantity || 0}.`,true);
  if(remove){record.quantity-=quantity;if(record.quantity===0)records=records.filter(r=>r!==record);}
  else if(record){if(record.quantity+quantity>999999)return message("Demo quantity limit reached.",true);record.quantity+=quantity;}
  else records.push({category,item,room,quantity});
  $("search").value="";$("filter-room").value="";renderRows();message(`${remove ? "Removed" : "Added"} ${quantity} × ${item} ${remove ? "from" : "to"} ${room}.`);
});
$("search-form").addEventListener("submit",e=>{e.preventDefault();renderRows();message("Search results updated.");});
$("search").addEventListener("input",renderRows);
$("filter-room").addEventListener("change",renderRows);
$("show-all").addEventListener("click",()=>{$("search").value="";$("filter-room").value="";renderRows();message("Showing all records in this view.");});
$("reset").addEventListener("click",()=>$("reset-dialog").showModal());
$("reset-dialog").addEventListener("close",()=>{if($("reset-dialog").returnValue!=="reset")return;seed();category="Furniture";$("quantity").value="1";render();message("Demo reset. Original sample inventory restored.");});
seed();render();
