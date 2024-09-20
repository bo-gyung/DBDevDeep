import {
    ClassicEditor,
    AccessibilityHelp,
    AutoImage,
    Autosave,
    BlockQuote,
    Bold,
    Essentials,
    Heading,
    ImageBlock,
    ImageCaption,
    ImageInline,
    ImageInsert,
    ImageInsertViaUrl,
    ImageResize,
    ImageStyle,
    ImageTextAlternative,
    ImageToolbar,
    ImageUpload,
    Indent,
    IndentBlock,
    Italic,
    List,
    ListProperties,
    Paragraph,
    SelectAll,
    SimpleUploadAdapter,
    Table,
    TableCaption,
    TableCellProperties,
    TableColumnResize,
    TableProperties,
    TableToolbar,
    TodoList,
    Underline,
    Undo
} from 'ckeditor5';

import translations from 'ckeditor5/translations/ko.js';

let editorInstance;

// CKEditor 초기화
ClassicEditor.create(document.querySelector('#editor'), {
    toolbar: {
        items: [
            'undo', 'redo', '|', 'heading', '|', 'bold', 'italic', 'underline', '|',
            'insertImage', 'insertTable', 'blockQuote', '|',
            'bulletedList', 'numberedList', 'todoList', 'outdent', 'indent'
        ]
    },
    plugins: [
        AccessibilityHelp, AutoImage, Autosave, BlockQuote, Bold, Essentials, Heading,
        ImageBlock, ImageCaption, ImageInline, ImageInsert, ImageInsertViaUrl, ImageResize, ImageStyle,
        ImageTextAlternative, ImageToolbar, ImageUpload, Indent, IndentBlock, Italic, List,
        ListProperties, Paragraph, SelectAll, SimpleUploadAdapter, Table, TableCaption,
        TableCellProperties, TableColumnResize, TableProperties, TableToolbar, TodoList, Underline, Undo
    ],
    image: {
        toolbar: [
            'imageTextAlternative', 'toggleImageCaption', '|',
            'imageStyle:inline', 'imageStyle:wrapText', 'imageStyle:breakText', '|',
            'resizeImage'
        ]
    },
    language: 'ko',
    placeholder: '여기에 내용을 입력하세요',
    translations: [translations]
}).then(editor => {
    editorInstance = editor;
}).catch(error => {
    console.error('CKEditor 초기화 오류:', error);
});

// 템플릿 선택 시 AJAX로 데이터를 가져와 에디터에 삽입
$('#templateSelect').change(function() {
    var tempNo = $(this).val();
    if (tempNo) {
        $.ajax({
            url: '/getTempContent/' + tempNo,  // 서버에 템플릿 데이터를 요청하는 URL
            method: 'GET',
            success: function(data) {
                if (editorInstance) {
                    editorInstance.setData(data.content);  // 서버에서 받은 내용을 에디터에 삽입
                }
            },
            error: function(error) {
                console.error('템플릿 가져오기 실패:', error);
            }
        });
    } else {
        editorInstance.setData('');  // 선택값이 없을 경우 에디터 내용 초기화
    }
});