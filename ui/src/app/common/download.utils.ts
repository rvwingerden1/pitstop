export function downloadCsv(data: string[][], fileName = 'download.csv', mimeType = 'text/csv;encoding:utf-8') {
  const content = toCsvString(data);
  downloadAny(content, fileName, mimeType);
}

function toCsvString(data : any[][]) : string {
  let result = '';
  data.forEach((line, index) => {
    result += line.map(item => prepareLineItem(item)).join(',');
    if (index + 1 < data.length) {
      result += '\n';
    }
  });
  return result;

  function prepareLineItem(item: any) {
    item = item ? String(item) : '';
    if (item.indexOf(',') >= 0) {
      item = '"' + item;
    }
    if (item.indexOf(',') >= 0) {
      item = item + '"';
    }
    return item;
  }
}

export function downloadAny(content: any, fileName: string, mimeType: string) {
  const a = document.createElement('a');

  // if (navigator.msSaveBlob) { // IE10
  //   navigator.msSaveBlob(new Blob([content], {
  //     type: mimeType
  //   }), fileName);
  // } else
  if (URL && 'download' in a) { //html5 A[download]
    a.href = URL.createObjectURL(content instanceof Blob ? content : new Blob([content], {
      type: mimeType
    }));
    a.setAttribute('download', fileName);
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  } else {
    location.href = 'data:application/octet-stream,' + encodeURIComponent(content); // only this mime type is supported
  }
}
