# -*- coding: utf8 -*-
# encoding=utf8  
#import sys 
from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.screenmanager import ScreenManager, Screen
from kivy.properties import NumericProperty, ObjectProperty, StringProperty, ListProperty, BooleanProperty
from kivy.uix.floatlayout import FloatLayout
from kivy.uix.listview import ListItemButton
from kivy.uix.selectableview import SelectableView
from kivy.uix.togglebutton import ToggleButtonBehavior
from kivy.adapters.models import SelectableDataItem
from kivy.lang import Builder
from kivy.uix.label import Label
from kivy.core.window import Window
from kivy.uix.image import Image
from kivymd.theming import ThemeManager
from kivy.uix.recycleview import RecycleView
from kivy.uix.recycleview.views import RecycleDataViewBehavior
from kivymd.label import MDLabel

#reload(sys)  
#sys.setdefaultencoding('utf8')
#kivy.require('1.9.1')
    

Builder.load_file('main.kv')

Image_des = ["Яблоня","Ясень","Береза","Тополь","Сирень","Барбарис","Пихта","Кедр","Можжевельник","Вяз","Бук"]
Image_dat = ["data/img_{0}.jpg".format(i) for i in range(11)]
description = ["Описание {0}".format(i) for i in range(11)]

items = [{"text": i,"on_state":'normal',"image": j,"description": k} for i,j,k in zip(Image_des,Image_dat,description)]

class MyViewClass(RecycleDataViewBehavior, BoxLayout):

    text = StringProperty("")
    image = StringProperty("")
    description = StringProperty("")
    index = None

    def set_state(self,state,app):
        app.root.ids.rv.data[self.index]['selected'] = state

    def refresh_view_attrs(self, rv, index, data):
        self.index = index
        return super(MyViewClass, self).refresh_view_attrs(rv, index, data)



class MyRecycleView(RecycleView):

    data = items

    
            

class AnswersListItem(ToggleButtonBehavior, SelectableView, BoxLayout):
    ''' 
    This is Answers Class
    
    '''
    name = StringProperty()
    disabled = BooleanProperty()
    count = 1
    l = []
        
    def __repr__(self):
        return "%s(name=%r, disabled=%r)" % (type(self).__name__, self.name,self.disabled)
    
    def bar(self,data,state,name):
        for i in data:
            if i.name == name:
                i.is_selected = state
                
    def foo(self,x,y):
        app = App.get_running_app()
        self.bar(app.root.lst.adapter.data,x,y)
        
        

class QuestionsListItem(ToggleButtonBehavior, SelectableView, BoxLayout):
    ''' 
    This is Quesion Class
    
    '''    
    name = StringProperty()
    def __repr__(self):
        return "%s(name=%r)" % (type(self).__name__, self.name)

    def print_answers(self,q, value,answ,app):
        for k, v in q.items():
            if v == value:
                key =  k
                break
        for k,v in answ.items():
            if k == key:
                answer = v
                break
        app.root.lst.adapter.data = []   
        app.root.lst.adapter.data.extend([AnswersDataItem(i, disabled=False, is_selected=False) for i in answer])
        app.root.lst._trigger_reset_populate()    

    def on_state(self, me, state):
#__________Questions LISTADAPTER EVENTS_________________________________________
        app = App.get_running_app()
        ques = app.root.ques
        answ = app.root.answ
        app.root.lbl.text = me.name
        self.print_answers(ques,me.name,answ,app)
#_______________________________________________________________________________
        if state == "down":
            self.select()
        else:
            self.deselect()


class AnswersDataItem(SelectableDataItem):
    def __init__(self, name, disabled, **kwargs):
        super(AnswersDataItem, self).__init__(**kwargs)
        self.name = name
        self.disabled = disabled

    def __repr__(self):
        return "%s(name=%r, is_selected=%r, disabled=%r)" % (type(self).__name__, self.name, self.is_selected,self.disabled)
    
    
class DataItem(SelectableDataItem):
    def __init__(self, name, **kwargs):
        super(DataItem, self).__init__(**kwargs)
        self.name = name

    def __repr__(self):
        return "%s(name=%r, is_selected=%r)" % (type(self).__name__, self.name, self.is_selected)
    
    
class RootWidget(FloatLayout):
    ''' 
    This is Main Class
    
    '''    
    text_color = [0,0,0,1]
    manager = ObjectProperty()
    txt_input_all = ObjectProperty()
    txt_input_rec = ObjectProperty()
    data = ListProperty()
    data2 = ListProperty()
    lst = ObjectProperty()
    lbl = ObjectProperty()
    lv = ObjectProperty()
    
    
    
    ques={1:'Хвоя или листва?', 2:'Форма листьев?', 3:'Сложный или простой\n  лист по форме?', 4:'Цвет коры', 5:'Откуда первые\nбоковые веточки?', 6:'Чечевички', 7:'Древовидный ствол?', 8:'Окрас цветов', 9:'Цветы,\nкол-во лепестков', 10:'Цветы, форма', 11:'Плод-одиночный\nили гроздь?', 12:'Край листа', 13:'Тип соцветия', 14:'Щетинистые почки?', 15:'Расположение - \nпопарное или нет?', 16:'Есть ли "ножки"?'}
    answ = {1:['Хвоя','Листва'],2:['Широкоэллепти\nческий', 'Заостренный','Ланцетные','Эллиптический','Пальчатосложные','Пальчаторассе\nчённый','Игольчатая','Непарноперисто\nсложные','Игловидный','Яйцевидно\nланцетные','Овальные','Яйцевидно-\nромбические','Яйцевидно-\nтреугольные','Широко\nяйцевидные','Супротивные'],3:[],4:[],5:[],6:[],7:[],8:[],9:[],10:[],11:[],12:[],13:[],14:[],15:[],16:[]}
    current = {}
    item = []
    stored_answ = {}
    c = [1,1,1,0]
    
    
        
    def __init__(self,**kwargs):
        super(RootWidget, self).__init__(**kwargs)
        self.data = [DataItem(i, is_selected=False) for i in self.ques.values()]
        
        
    def args_converter(self, index, data_item):
        return {
            "index": index,
             "name": data_item.name,
        }
    
    def answers_args_converter(self, index, data_item):
        return {
            "index": index,
             "name": data_item.name,
             "disabled": data_item.disabled,
        }    
    
    
    
    def search_in_all(self):
        '''
        
        Search in All Question List
        
        '''
        txt = self.txt_input_all.text
        l = []
        for k, v in self.ques.items():
            try: 
                txt.decode('utf-8')
            except BaseException:
                if txt.lower() in v.lower():
                    l.append(v)
            else:
                if txt.decode('utf-8').lower() in v.decode('utf-8').lower():
                    l.append(v)
        self.lv.adapter.data = []
        if len(l) == 0:
            l.append('Ничего не найдено')
        self.lv.adapter.data.extend([DataItem(i, is_selected=False) for i in l])
        self.lv._trigger_reset_populate()   
              
    def search_in_rec(self):
        '''
    
        Search in Recommended Question List
    
        '''        
        pass
    
    def send_results(self):
        '''
    
        Event for 'Sumbit' Button

        '''
        
        for i in self.lst.adapter.data:
            if i.is_selected:
                self.find_in_quest_and_disable(self.ques,self.answ,i.name)
        #print(self.lst.adapter.data)
            
    def find_in_quest_and_disable(self,quest_list,answer_list,answer):
        key = ""
        question = ""
        l = []
        for k,v in answer_list.items():
            if answer in v:
                key = k
                l = v
                break   
            
        for k, v in quest_list.items():
            if k == key:
                question = v
                break
            
        for i in range(len(self.lst.adapter.data)):
            if self.lst.adapter.data[i].name in l:
                self.lst.adapter.data[i].disabled = True
        self.stored_answ.update({question:answer})
        self.lst._reset_spopulate()
        
    def refresh_list(self):
        self.txt_input_all.text = ""
        self.txt_input_rec.text = ""
        self.lbl.text = ""
        self.lv.adapter.data = []
        self.lst.adapter.data = []   
        self.lv.adapter.data.extend([DataItem(i, is_selected=False) for i in self.ques.values()])
        self.lv._trigger_reset_populate() 
        
    def back(self):
        self.manager.current = 'Screen 1'
        #self.refresh_list()
        
class ActionApp(App):
    title = "Plants Recognizer"
    theme_cls = ThemeManager()
    def build(self):
        self.root = RootWidget()
        return self.root

myApp = ActionApp()

myApp.run()
