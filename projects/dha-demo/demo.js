import {Inventory,demoDate} from './state.mjs';
const db=new Inventory(),$=id=>document.getElementById(id);
const sections=['Dashboard','Add Item','Assign Item','Return Item','Store Inventory','User Inventory','Auction Inventory','Report','Low Stock','DHA Employee'];
let current='Dashboard',tab='Store';
const esc=v=>String(v??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const options=values=>values.map(v=>{const [value,label]=Array.isArray(v)?v:[v,v];return '<option value="'+esc(value)+'">'+esc(label)+'</option>'}).join('');
const field=(label,name,control)=>'<label class="field">'+label+control.replace('<input','<input name="'+name+'"').replace('<select','<select name="'+name+'"')+'</label>';
const input=(label,name,type='text',value='',extra='')=>field(label,name,'<input type="'+type+'" value="'+esc(value)+'" '+extra+'>');
const select=(label,name,values)=>field(label,name,'<select>'+options(values)+'</select>');
function status(text,error=false){$('status').textContent=text;$('status').dataset.error=String(error)}
function tabs(values){return '<div class="tabs" aria-label="Choose inventory view">'+values.map(v=>'<button type="button" data-tab="'+esc(v)+'" aria-pressed="'+(tab===v)+'">'+esc(v)+'</button>').join('')+'</div>'}
function table(title,headers,rows,search=true){return '<section class="card"><div class="table-top"><h2>'+esc(title)+'</h2>'+(search?'<label class="search"><span class="hint">Search this view</span><input type="search" id="search" placeholder="Item, employee or reference"></label>':'')+'</div><div class="table-wrap" tabindex="0" aria-label="Scrollable results"><table><caption id="caption">'+rows.length+' sample records</caption><thead><tr>'+headers.map(h=>'<th scope="col">'+esc(h)+'</th>').join('')+'</tr></thead><tbody id="rows">'+rows.map(row=>'<tr>'+row.map(c=>'<td>'+esc(c)+'</td>').join('')+'</tr>').join('')+'</tbody></table><p id="empty" class="empty" '+(rows.length?'hidden':'')+'>No matching records.</p></div></section>'}
function stockRows(kind){return db.items(kind).map(item=>{const t=db.totals(kind,item);return [item,t.received,t.available,t.assigned]})}
function employeeName(id){return db.employees.find(e=>e.id===id)?.name||'—'}
function metrics(values){return '<div class="metrics">'+values.map(([n,label])=>'<div class="metric"><strong>'+n+'</strong><span>'+label+'</span></div>').join('')+'</div>'}
function form(title,fields,button){return '<section class="card"><h2>'+title+'</h2><form id="workflow"><div class="form-grid">'+fields+'</div><div class="actions"><button>'+button+'</button><button type="reset" class="secondary">Clear form</button></div></form></section>'}
function render(){
 $('title').textContent=current;document.querySelectorAll('[data-section]').forEach(b=>b.setAttribute('aria-current',b.dataset.section===current?'page':'false'));
 let html='';
 if(current==='Dashboard'){
  const totals=db.items(tab).map(i=>db.totals(tab,i));const sum=k=>totals.reduce((n,t)=>n+t[k],0);
  const temp=db.issues.filter(i=>i.kind===tab&&i.type==='Temporary').reduce((n,i)=>n+i.quantity-i.returned,0);
  const perm=db.issues.filter(i=>i.kind===tab&&i.type==='Permanent').reduce((n,i)=>n+i.quantity,0);
  html=tabs(['Store','User','Auction'])+metrics(tab==='Store'?[[sum('received'),'Total store items'],[sum('available'),'In store'],[temp,'Temporary assignments'],[perm,'Permanent assignments']]:[[sum('received'),'Total '+tab.toLowerCase()+' items'],[sum('available'),tab==='User'?'Pending allocation':'In store'],[sum('assigned'),'Issued items']]);
  const rows=stockRows(tab).filter(r=>tab!=='Store'||(db.limits[r[0]]&&r[2]<db.limits[r[0]]));
  html+=table(tab==='Store'?'Low stock':tab==='User'?'User inventory':'Auction inventory',['Item name','Total received','Available','Assigned'],rows,false)+'<div class="quick-actions"><button data-go="Add Item">+ Receive stock</button><button data-go="Assign Item">Assign an item</button><button data-go="Report">View reports</button></div>';
 }else if(current==='Add Item'){
  html=tabs(['Store','User','Auction'])+form('Receive '+tab.toLowerCase()+' inventory',input('Item name','item','text','','required maxlength="100" list="item-names"')+input('Quantity','quantity','number','1','required min="1" max="9999" step="1"')+input('Received date','date','date',demoDate,'required')+input(tab==='Auction'?'Letter number':'Purchase order / voucher','reference','text','','required maxlength="100"')+(tab==='Store'?select('Received from','source',['Minute Sheet','Petty Cash','Replacement'])+input('Unit price (PKR)','price','number','0','min="0" max="10000000" step="0.01"'):input('Supplier / source','source','text','Sample Supplier','maxlength="100"'))+input('Remarks','remarks','text','','maxlength="100"'),'Add sample stock')+'<datalist id="item-names">'+options(db.items(tab))+'</datalist>';
 }else if(current==='Assign Item'){
  html=tabs(['Store','User','Auction'])+form('Assign '+tab.toLowerCase()+' inventory',select('Item · available quantity','item',db.items(tab).filter(i=>db.totals(tab,i).available>0).map(i=>[i,i+' · '+db.totals(tab,i).available+' available']))+input('Quantity','quantity','number','1','required min="1" max="9999" step="1"')+(tab!=='Auction'?select('Employee','employee',db.employees.map(e=>[e.id,e.name+' / '+e.branch])):'')+(tab==='Store'?select('Assignment type','type',['Temporary','Permanent']):'')+input('Issued date','date','date',demoDate,'required')+input('Register / reference number','reference','text','','required maxlength="100"')+input('Remarks','remarks','text','','maxlength="100"'),'Assign sample item');
 }else if(current==='Return Item'){
  const allocations=db.issues.filter(i=>i.kind==='Store'&&i.type==='Temporary'&&i.returned<i.quantity);
  html='<p class="hint">Return items from a temporary allocation. Available stock updates immediately.</p>'+form('Return allocated stock',select('Temporary allocation','issue',allocations.map(i=>[i.id,i.item+' / '+employeeName(i.employee)+' / '+(i.quantity-i.returned)+' outstanding']))+input('Return quantity','quantity','number','1','required min="1" max="9999" step="1"')+input('Return date','date','date',demoDate,'required')+input('Remarks','remarks','text','','maxlength="100"'),'Return stock');
 }else if(current.endsWith('Inventory')){
  const kind=current.split(' ')[0];const choices=kind==='Store'?['Store','Received','Temporary','Permanent']:kind==='User'?['Overview','Pending','Issued']:['Overview','Received','Issued'];
  html=tabs(choices);let rows,headers;
  if(['Store','Overview'].includes(tab)){rows=stockRows(kind);headers=['Item name','Total received','Available','Assigned'];}
  else if(tab==='Received'){rows=db.receipts.filter(r=>r.kind===kind).map(r=>[r.item,r.quantity,r.source,r.reference,r.date]);headers=['Item name','Quantity','Source','Reference','Date'];}
  else if(tab==='Pending'){rows=stockRows(kind).filter(r=>r[2]>0).map(r=>[r[0],r[2]]);headers=['Item name','Pending quantity'];}
  else{rows=db.issues.filter(i=>i.kind===kind&&(tab==='Issued'||i.type===tab)).map(i=>[i.item,i.quantity,i.returned,i.quantity-i.returned,employeeName(i.employee),i.reference,i.date]);headers=['Item name','Issued','Returned','Outstanding','Employee','Reference','Date'];}
  html+=table(kind+' inventory · '+tab.toLowerCase(),headers,rows);
 }else if(current==='Low Stock'){
  html=form('Set a low-stock alert',select('Store item','item',db.items('Store'))+input('Alert threshold','quantity','number','5','required min="1" max="9999" step="1"'),'Save threshold')+table('Monitored inventory',['Item name','In store','Alert threshold','Status'],Object.entries(db.limits).map(([i,n])=>[i,db.totals('Store',i).available,n,db.totals('Store',i).available<n?'Low stock':'Sufficient']));
 }else if(current==='DHA Employee'){
  html=form('Add a sample employee',input('Employee ID','id','text','','required maxlength="100"')+input('Employee name','name','text','','required maxlength="100"')+select('Branch','branch',['Administration','IT','Operations'])+input('Designation','designation','text','','required maxlength="100"'),'Add employee')+table('Employee directory',['Employee ID','Name','Branch','Designation'],db.employees.map(e=>[e.id,e.name,e.branch,e.designation]));
 }else if(current==='Report'){
  html='<section class="card"><h2>Inventory movement report</h2><form id="report-filter" class="report-filters">'+select('Inventory','kind',['All','Store','User','Auction'])+input('From','from','date','2026-11-01','required')+input('To','to','date',demoDate,'required')+'<button>Apply filters</button><button type="button" id="export" class="secondary">Download CSV</button></form><p class="hint">Receipts, issues and returns for the selected period.</p></section><div id="report-results"></div>';
 }
 $('content').innerHTML=html;
 if(current==='Report')renderReport();
 $('workflow')?.addEventListener('submit',event=>{event.preventDefault();const d=Object.fromEntries(new FormData(event.currentTarget));d.kind=tab;try{const action={'Add Item':'receive','Assign Item':'assign','Return Item':'returnStock','Low Stock':'limit','DHA Employee':'employee'}[current];db[action](d);render();status({'receive':'Sample stock received.','assign':'Sample item assigned.','returnStock':'Item returned and stock updated.','limit':'Low-stock threshold saved.','employee':'Sample employee added.'}[action]);}catch(e){status(e.message,true)}});
 $('report-filter')?.addEventListener('submit',event=>{event.preventDefault();renderReport()});
 $('export')?.addEventListener('click',exportReport);
 attachSearch();
}
function attachSearch(){const search=$('search');search?.addEventListener('input',()=>{let visible=0;document.querySelectorAll('#rows tr').forEach(row=>{row.hidden=!row.textContent.toLowerCase().includes(search.value.toLowerCase().trim());if(!row.hidden)visible++});$('empty').hidden=visible>0;$('caption').textContent=visible+' matching records';})}
function reportData(){const d=Object.fromEntries(new FormData($('report-filter')));db.date(d.from);db.date(d.to);if(d.from>d.to)throw Error('The start date must be before the end date.');return [...db.receipts.map(r=>({...r,movement:'Received'})),...db.issues.map(r=>({...r,movement:'Issued'})),...db.returns.map(r=>({...r,movement:'Returned'}))].filter(r=>(d.kind==='All'||r.kind===d.kind)&&r.date>=d.from&&r.date<=d.to).sort((a,b)=>b.date.localeCompare(a.date)).map(r=>[r.date,r.kind,r.movement,r.item,r.quantity,r.reference||'—']);}
function renderReport(){try{$('report-results').innerHTML=table('Movement history',['Date','Inventory','Movement','Item name','Quantity','Reference'],reportData());attachSearch();status('Report updated.');}catch(e){status(e.message,true)}}
function exportReport(){try{const rows=[['Date','Inventory','Movement','Item','Quantity','Reference'],...reportData()];const csv=rows.map(r=>r.map(v=>'"'+String(v).replace(/^[=+@-]/,"'$&").replace(/"/g,'""')+'"').join(',')).join('\r\n');const url=URL.createObjectURL(new Blob([csv],{type:'text/csv;charset=utf-8'}));const a=document.createElement('a');a.href=url;a.download='dha-sample-inventory-report.csv';a.click();setTimeout(()=>URL.revokeObjectURL(url),1000);status('Sample report downloaded.');}catch(e){status(e.message,true)}}
function navigate(section){current=section;tab=section==='User Inventory'||section==='Auction Inventory'?'Overview':'Store';render();status('Viewing '+section.toLowerCase()+'.');}
$('navigation').innerHTML=sections.map(s=>'<button type="button" data-section="'+s+'">'+s+'</button>').join('');
document.addEventListener('click',event=>{const section=event.target.closest('[data-section]'),go=event.target.closest('[data-go]'),choice=event.target.closest('[data-tab]');if(section)navigate(section.dataset.section);if(go)navigate(go.dataset.go);if(choice){tab=choice.dataset.tab;render();status('Viewing '+tab.toLowerCase()+'.');}});
$('reset').addEventListener('click',()=>$('reset-dialog').showModal());$('reset-dialog').addEventListener('close',()=>{if($('reset-dialog').returnValue==='reset'){db.reset();navigate('Dashboard');status('Original sample inventory restored.');}});
render();
