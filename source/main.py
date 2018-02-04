# -*- coding: utf8 -*-
# encoding=utf8  
import sys  
import kivy
from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.screenmanager import ScreenManager, Screen
from kivy.properties import NumericProperty, ObjectProperty
from kivy.uix.floatlayout import FloatLayout
from kivy.uix.listview import ListItemButton
from kivy.properties import StringProperty, ListProperty
from kivy.uix.selectableview import SelectableView
from kivy.uix.togglebutton import ToggleButtonBehavior
from kivy.adapters.models import SelectableDataItem
from kivy.lang import Builder
from kivy.uix.label import Label
from kivy.core.window import Window

reload(sys)  
sys.setdefaultencoding('utf8')
kivy.require('1.9.1')

Builder.load_file('main.kv')

class DataImageItem(SelectableDataItem):
    '''
    
    Image ConvertData Class
    
    '''
    def __init__(self, name,img, **kwargs):
        super(DataImageItem, self).__init__(**kwargs)
        self.name = name
        self.img = img

    def __repr__(self):
        return "{0}(name={1}, img={3}, is_selected={2})".format(type(self).__name__, self.name, self.is_selected,self.img)
    

class ImageListItem(ToggleButtonBehavior, SelectableView, BoxLayout):
    '''
    
    Image ListAdapter Class
    
    '''
    name = StringProperty()
    img = StringProperty()
    def __repr__(self):
        return "{0}(name={1},img={2})".format(type(self).__name__, self.name,self.img)

    def on_state(self, me, state):
        #print 'Select'
        #if state == "down":
            #self.select()
        #else:
            #self.deselect()
        pass
            

class AnswersListItem(ToggleButtonBehavior, SelectableView, BoxLayout):
    ''' 
    This is Answers Class
    
    '''
    name = StringProperty()

    def __repr__(self):
        return "%s(name=%r)" % (type(self).__name__, self.name)

    def on_state(self, me, state):
        print me, state
        if state == "down":
            self.select()
        else:
            self.deselect()


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
        app.root.lst.adapter.data.extend([DataItem(i, is_selected=False) for i in answer])
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
    manager = ObjectProperty()
    txt_input_all = ObjectProperty()
    txt_input_rec = ObjectProperty()
    data = ListProperty()
    data2 = ListProperty()
    lst = ObjectProperty()
    lbl = ObjectProperty()
    lv = ObjectProperty()
    data3 = ListProperty()
    
    
    Image_des = ["Яблоня","Ясень","Береза","Тополь","Сирень","Барбарис","Пихта","Кедр","Можжевельник","Вяз","Бук"]
    Image_dat = ["data/img_{0}.jpg".format(i) for i in range(11)]
    ques={1:'Хвоя или листва?', 2:'Форма листьев?', 3:'Сложный или простой\n  лист по форме?', 4:'Цвет коры', 5:'Откуда первые\nбоковые веточки?', 6:'Чечевички', 7:'Древовидный ствол?', 8:'Окрас цветов', 9:'Цветы,\nкол-во лепестков', 10:'Цветы, форма', 11:'Плод-одиночный\nили гроздь?', 12:'Край листа', 13:'Тип соцветия', 14:'Щетинистые почки?', 15:'Расположение - \nпопарное или нет?', 16:'Есть ли "ножки"?'}
    answ = {1:['Хвоя','Листва'],2:['Широкоэллепти\nческий', 'Заостренный','Ланцетные','Эллиптический','Пальчатосложные','Пальчаторассе\nчённый','Игольчатая','Непарноперисто\nсложные','Игловидный','Яйцевидно\nланцетные','Овальные','Яйцевидно-\nромбические','Яйцевидно-\nтреугольные','Широко\nяйцевидные','Супротивные'],3:[],4:[],5:[],6:[],7:[],8:[],9:[],10:[],11:[],12:[],13:[],14:[],15:[],16:[]}
    item = []    
    
    for i,j in zip(Image_des,Image_dat):
        item.append((i,j))
        
    def __init__(self,**kwargs):
        super(RootWidget, self).__init__(**kwargs)
        self.data = [DataItem(i, is_selected=False) for i in self.ques.values()]
        self.data3 = [DataImageItem(i,j,is_selected=False) for i,j in self.item]
        
        
    def args_converter(self, index, data_item):
        return {
            "index": index,
             "name": data_item.name,
        }
    
    
    def image_args_converter(self, index, data_item):
        return {
            "index": index,
             "name": data_item.name,
             "img": data_item.img,
        }
    
    
    def search_in_all(self):
        '''
        
        Search in All Question List
        
        '''
        txt = self.txt_input_all.text
        l = []
        for k, v in self.ques.items():
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
        pass

    def refresh_list(self):
	self.txt_input_all.text = ""
    	self.txt_input_rec.text = ""
	self.lbl.text = ""
        self.lv.adapter.data = []
	self.lst.adapter.data = []   
        self.lv.adapter.data.extend([DataItem(i, is_selected=False) for i in self.ques.values()])
        self.lv._trigger_reset_populate() 
        
class ActionApp(App):
    title = "Plants Recognizer"
    def build(self):
        self.root = RootWidget()
        return self.root

myApp = ActionApp()

myApp.run()
