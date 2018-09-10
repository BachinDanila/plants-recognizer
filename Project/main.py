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
from random import shuffle

#reload(sys)  
#sys.setdefaultencoding('utf8')
#kivy.require('1.9.1')
    

Builder.load_file('main.kv')

Image_des = ["Яблоня","Ясень","Береза","Тополь","Сирень","Барбарис","Пихта","Кедр","Можжевельник","Вяз","Бук"]
Image_dat = ["data/img_{0}.jpg".format(i) for i in range(11)]
description = ["""
                                                                                            Я́блоня (лат. Mālus) — род листопадных деревьев и кустарников
                                                                                            семейства Розовые (Rosaceae) с шаровидными 
                                                                                            сладкими или кисло-сладкими плодами.
"""
,"""
                                                                    Я́сень (лат. Fraxinus) — род древесных растений из 
                                                                    семейства Маслиновые (Oleaceae).

""",
"""
                                                                                            Берёза (лат. Bétula) — род листопадных деревьев и кустарников
                                                                                            семейства Берёзовые (Betulaceae).
                                                                                            Берёза широко распространена в Северном полушарии; 
                                                                                            на территории России принадлежит к числу 
                                                                                            наиболее распространённых древесных пород
""",
"""
                                                                                 То́поль (лат. Pópulus) — род двудомных листопадных 
                                                                                 быстрорастущих деревьев семейства Ивовые (Salicaceae).
                                                                                 Лес с преобладанием тополей называют тополёвником.
""",
"""
                                                                            Сире́нь (лат. Syrínga) — род кустарников, 
                                                                            принадлежащий семейству Маслиновые (лат. Oleaceae). 
                                                                            Род включает около тридцати видов, распространённых
                                                                            в Юго-Восточной Европе (Венгрия, Балканы) и в Азии.
""",
"""
                                                                            Барбари́с (лат. Bérberis) — крупный род кустарников, 
                                                                            реже деревьев, семейства Барбарисовые (Berberidaceae)
""",
"","","","",""]

items = [{"text": i,"on_state":'normal',"image": j,"description": k} for i,j,k in zip(Image_des,Image_dat,description)]

class MyViewClass(RecycleDataViewBehavior, BoxLayout):

    text = StringProperty("")
    image = StringProperty("")
    description = StringProperty("")
    index = None
    
    def pressed(self):
        app = App.get_running_app()
        app.root.manager.current = "Screen 8"
        app.root.toolbar.title = "Яблоня"
    
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
        self.bar(app.root.lst_2.adapter.data,x,y)
        

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
        app.root.lst_2.adapter.data = []   
        app.root.lst_2.adapter.data.extend([AnswersDataItem(i, disabled=False, is_selected=False) for i in answer])
        app.root.lst_2._trigger_reset_populate()          

    def on_state(self, me, state):
#__________Questions LISTADAPTER EVENTS_________________________________________
        app = App.get_running_app()
        ques = app.root.ques
        answ = app.root.answ
        app.root.lbl.text = me.name
        app.root.lbl_2.text = me.name
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
    
    a = """
    Яблоня(лат. Mālus) — род листопадных деревьев и кустарников семейства Розовые 
    (Rosaceae) с шаровидными сладкими или кисло-сладкими плодами.
    Происходит из зон умеренного климата Северного полушария.
    Род относится к трибе Яблоневые (Maleae) подсемейства Сливовые (Spiraeoideae). 
    Насчитывает 62 вида (2013). Наиболее распространены: 
    яблоня домашняя, или культурная (Malus domestica), 
    к которой относится большинство возделываемых в мире сортов 
    (число которых превышает 10 тысяч), яблоня сливолистная, 
    китайская (Malus prunifolia) и яблоня низкая (Malus pumila).
    Многие виды яблони выращивают в качестве декоративных растений в садах и парках, 
    используют в полезащитном лесоразведении. Все виды — хорошие медоносы. 
    Древесина у яблони плотная, крепкая, легко режется и хорошо полируется; 
    пригодна для токарных и столярных изделий, мелких поделок.
    
    Название:
    Русское слово яблоко возникло в результате прибавления протетического 
    начального j- к праслав. *ablъko; последнее образовано с помощью суффикса 
    -ъk- от позднепраиндоевропейской основы *āblu- ‘яблоко’ 
    (к той же основе восходят лит. obuolỹs, латыш. ābols, англ. apple, 
    нем. Apfel, галльск. avallo, др.‑ирл. aball). 
    Данная основа представляет собой регионализм северо-западных 
    индоевропейских языков и восходит, в свою очередь, к общеиндоевропейской основе 
    (реконструируемой как *(a)masl или как *ŝamlu). 
    С суффиксом -onь- та же основа дала яблонь (позднейшее яблоня).
    
    Ботаническое описание:
    Деревья с развесистой кроной высотой 2,5—15 м. Ветви — укороченные (плодущие), 
    на которых закладываются цветочные почки, и удлинённые (ростовые). 
    У дикорастущих видов на ветвях имеются колючки.
    Листья черешковые, голые или опушённые снизу, 
    с опадающими или остающимися прилистниками.
    Цветки собраны в немногоцветковые полузонтиковидные или щитковидные соцветия. 
    Окраска цветков может изменяться от совершенно белой до нежно-розовой и ярко-малиновой. 
    Цветки яблони протогиничны: гинецей созревает раньше андроцея. Опыляются насекомыми.
    Плод — яблоко, возникающее из нижней завязи. Гинецей заключён в нижнюю завязь. 
    По мере формирования плода плодолистики становятся хрящеватыми, пергаментными, кожистыми. 
    На разрезе плода чётко видна граница между тканями гипантия и тканями завязи, о
    черченная окружностью более плотно расположенных клеток и сосудистых пучков.
    
    Распространение:
    Всего на территории бывшего СССР известно свыше десяти видов. 
    Из дикорастущих видов в лесах Европейской части и на Кавказе произрастает яблоня лесная 
    (Malus sylvestris); в Малой Азии, Иране, Крыму и на Кавказе — яблоня восточная 
    (Malus orientalis); в Китае, Монголии, Приморском крае, Восточной Сибири — яблоня ягодная 
    (Malus baccata); в лесах Тянь-Шаня — яблоня Недзвецкого (Malus niedzwetzkyana).
    
    История разведения:
    Дикорастущими яблоками предки современного человека питались всегда. 
    Родиной одомашненной яблони является территория современного южного Казахстана и Киргизии 
    (предгорья Алатау), где до сих пор встречается в диком виде яблоня Сиверса, 
    от которой и произошла яблоня домашняя. 
    Предположительно, оттуда во времена Александра Македонского либо во время иных миграций 
    она попала в Европу. По другой версии, это фруктовое дерево первоначально произрастало 
    в районе между Каспийским и Чёрным морями, а уже оттуда было завезено в другие районы мира.
    Обугленные остатки яблони обнаружены при раскопках доисторических озёрных стоянок Швейцарии; 
    по-видимому, жители Европы хорошо знали яблоню ещё во времена неолита, 
    но одомашнивание её в данной части света произошло значительно позднее.
    Колыбелью яблоневодства в Европе была Древняя Греция. 
    Писатели Древнего Рима — Катон, Варрон, Колумелла, Плиний Старший — описывали 36 сортов яблони, 
    выращиваемых в их время. В европейской культуре яблоня быстро заняла важное место. 
    Общеевропейским можно считать мотив «золотых яблок», 
    якобы дарующих бессмертие и вечную молодость и потому часто похищаемых. 
    Даже слово «рай» по-кельтски звучит как Авалон («страна яблок»).
    В русских землях культурная яблоня впервые появилась в XI веке в монастырских садах Киевской Руси; 
    так, при Ярославе Мудром (в 1051 году) был заложен яблоневый сад, 
    позже известный как сад Киево-Печерской лавры. 
    В XVI веке яблоня появилась и в северных районах Руси. 
    Для выведения культурных сортов яблони были использованы четыре её вида: 
    яблоня низкая, яблоня лесная, яблоня ягодная и яблоня сливолистная, или китайская.
    """
    
    text_color = [0,0,0,1]
    c = [1,1,1,0]
    
    manager = ObjectProperty()
    txt_input_all = ObjectProperty()
    txt_input_rec = ObjectProperty()
    data = ListProperty()
    data2 = ListProperty()
    data_shuffle = ListProperty()
    lst = ObjectProperty()
    lbl = ObjectProperty()
    lbl_2 = ObjectProperty()
    lst_2 = ObjectProperty()
    lv = ObjectProperty()
    lv_2 = ObjectProperty()
    toolbar = ObjectProperty()
    
    
    
    ques={1:'Хвоя или листва?', 2:'Форма листьев?', 3:'Сложный или простой\n  лист по форме?', 4:'Цвет коры', 5:'Откуда первые\nбоковые веточки?', 6:'Чечевички', 7:'Древовидный ствол?', 8:'Окрас цветов', 9:'Цветы,\nкол-во лепестков', 10:'Цветы, форма', 11:'Плод-одиночный\nили гроздь?', 12:'Край листа', 13:'Тип соцветия', 14:'Щетинистые почки?', 15:'Расположение - \nпопарное или нет?', 16:'Есть ли "ножки"?'}
    answ = {1:['Хвоя','Листва'],2:['Широкоэллепти\nческий', 'Заостренный','Ланцетные','Эллиптический','Пальчатосложные','Пальчаторассе\nчённый','Игольчатая','Непарноперисто\nсложные','Игловидный','Яйцевидно\nланцетные','Овальные','Яйцевидно-\nромбические','Яйцевидно-\nтреугольные','Широко\nяйцевидные','Супротивные'],3:[],4:[],5:[],6:[],7:[],8:[],9:[],10:[],11:[],12:[],13:[],14:[],15:[],16:[]}
    current = {}
    item = []
    stored_answ = {}
    
    def press_button(self,arg):
        if arg == "Recommended Questions":
            self.manager.current = "Screen 2"
            
        elif arg == "All Questions":
            self.manager.current = "Screen 3"
            
        elif arg == "Popular":
            self.manager.current = "Screen "
            
        elif arg == "Catalogue":
            self.manager.current = "Screen 6"
            
        elif arg == "Settings":
            self.manager.current = "Screen 5"
            
        elif arg == "Info":
            self.manager.current = "Screen 4"
            
        self.toolbar.title = arg
        
    def __init__(self,**kwargs):
        super(RootWidget, self).__init__(**kwargs)
        self.data = [DataItem(i, is_selected=False) for i in self.ques.values()]
        a = list(self.ques.values())
        shuffle(a)
        self.data_shuffle = [DataItem(i, is_selected=False) for i in a]
        
        
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
        txt = self.txt_input_rec.text
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
        self.lv_2.adapter.data = []
        if len(l) == 0:
            l.append('Ничего не найдено')
        self.lv_2.adapter.data.extend([DataItem(i, is_selected=False) for i in l])
        self.lv_2._trigger_reset_populate()
    
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
        self.toolbar.title = "PlantsRecognizer"
        self.txt_input_all.text = ""
        self.txt_input_rec.text = ""
        self.lbl.text = ""
        self.lbl_2.text = ""
        self.lv.adapter.data = []
        self.lv_2.adapter.data = []
        self.lst.adapter.data = [] 
        self.lst_2.adapter.data = []
        a = list(self.ques.values())
        shuffle(a)
        self.lv.adapter.data.extend([DataItem(i, is_selected=False) for i in a])
        self.lv._trigger_reset_populate() 
        self.lv_2.adapter.data.extend([DataItem(i, is_selected=False) for i in self.ques.values()])
        self.lv_2._trigger_reset_populate()         
        
    def back(self):
        self.manager.current = 'Screen 1'
        self.refresh_list()
        
class ActionApp(App):
    title = "Plants Recognizer"
    theme_cls = ThemeManager()
    def build(self):
        self.root = RootWidget()
        return self.root

myApp = ActionApp()

myApp.run()
